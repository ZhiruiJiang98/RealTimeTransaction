package dev.codescreen.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.model.constant.ActionResponseStatus;
import dev.codescreen.library.model.constant.ActionType;
import dev.codescreen.library.model.constant.ResponseCode;
import dev.codescreen.library.model.dto.AccountDto;
import dev.codescreen.library.model.dto.TransactionDto;
import dev.codescreen.library.model.server.*;
import dev.codescreen.library.mysql.MysqlClient;
import dev.codescreen.library.storage.AccountStorageManager;
import dev.codescreen.library.storage.TransactionStorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.money.MonetaryAmount;
import javax.money.UnknownCurrencyException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class CreateLoadTransactionAction implements AbstractAction<APIGatewayProxyRequestEvent, LoadTransactionResponse>{
    private final Logger LOGGER = LogManager.getLogger(CreateLoadTransactionAction.class);
    private final String host;
    private final String user;
    private final String pwd;
    private final MysqlClient client;
    private final TransactionStorageManager transactionStorageManager;
    private final AccountStorageManager accountStorageManager;

    @Inject
    public CreateLoadTransactionAction(TransactionStorageManager transactionStorageManager,
                                       AccountStorageManager accountStorageManager,
                                       @Named("MysqlHost") String host,
                                       @Named("MysqlUser") String user,
                                       @Named("MysqlPassword") String pwd,
                                       MysqlClient client) {
        this.transactionStorageManager = transactionStorageManager;
        this.accountStorageManager = accountStorageManager;
        this.host = host;
        this.user = user;
        this.pwd = pwd;
        this.client = client;
    }

    @Override
    public String getActionName() {
        return "CreateLoadTransactionAction";
    }

    @Override
    public ActionResponse<LoadTransactionResponse> processRequest(APIGatewayProxyRequestEvent event) throws SQLException{
        if(!requestValidated(event)){
            return constructResponse(
                    ActionType.LOAD.actionName,
                    ActionResponseStatus.BAD_REQUEST,
                    "Invalid request",
                    null,
                    getActionName()
            );
        }
        try{
            LOGGER.info("Starting Load transaction...");
            Gson gson = new Gson();
            this.client.connect(this.host, this.user, this.pwd);
            this.client.setAutoCommit(false);

            LoadTransactionRequest request = gson.fromJson(event.getBody(), LoadTransactionRequest.class);
            String currentTimeStamp = String.valueOf(System.currentTimeMillis());

            AccountDto currentAccount = getCurrentAccount(request.getUserId());
            if(currentAccount == null){
                return constructResponse(
                        ActionType.LOAD.actionName,
                        ActionResponseStatus.NOT_FOUND,
                        "Account not found",
                        null,
                        getActionName()
                );
            }

            if(isTransactionDuplicate(request.getMessageId())){
                LOGGER.info("Transaction already exists");
                return handleDuplicateTransaction(request, currentAccount, currentTimeStamp);
            }

            CurrencyExchanger currencyExchanger = new CurrencyExchanger();
            BigDecimal currentBalance = new BigDecimal(currentAccount.getBalance());
            MonetaryAmount exchangeAmount = currencyExchanger.exchange(
                    currentAccount.getCurrency(),
                    request.getTransactionAmount().getCurrency(),
                    request.getTransactionAmount().getAmount());
            BigDecimal currentTransactionAmount = exchangeAmount.getNumber().numberValue(BigDecimal.class);
            TransactionDto currentTransaction= createTransactionDto(request, currentAccount);
            String currentBalanceAfterTransaction = currentTransactionAmount.add(currentBalance).setScale(2, RoundingMode.CEILING).toString();
            if(processTransaction(currentTransaction, currentAccount, currentBalanceAfterTransaction)){
                client.commit();
                return constructResponse(
                        ActionType.LOAD.actionName,
                        ActionResponseStatus.CREATED,
                        "",
                        LoadTransactionResponse.builder()
                                .userId(request.getUserId())
                                .messageId(request.getMessageId())
                                .balance(
                                        Balance.builder()
                                                .amount(currentBalanceAfterTransaction)
                                                .currency(currentAccount.getCurrency())
                                                .debitOrCredit(request.getTransactionAmount().getDebitOrCredit())
                                                .build()
                                ).build(),
                        getActionName()
                );
            } else {
                this.client.rollback();
                return constructResponse(
                        ActionType.LOAD.actionName,
                        ActionResponseStatus.INTERNAL_SERVER_ERROR,
                        "Error occurred while processing request",
                        null,
                        getActionName()
                );
            }


        } catch (SQLException ex){
            client.rollback();
            LOGGER.error("SQL Exception: " + ex.getMessage());
            throw ex;
        } finally{
            client.close();
        }
    }

    @Override
    public boolean requestValidated(APIGatewayProxyRequestEvent event) {
        LOGGER.info("Validating request...");

        if (event.getPathParameters() == null || event.getBody() == null || event.getPathParameters().isEmpty() || event.getBody().isEmpty()) {
            return false;
        }
        try {
            Gson gson = new Gson();
            LoadTransactionRequest request = gson.fromJson(event.getBody(), LoadTransactionRequest.class);
            return request.syntacticallyValid() &&
                    !request.getUserId().isEmpty() &&
                    !request.getMessageId().isEmpty() &&
                    request.getTransactionAmount() != null &&
                    request.getTransactionAmount().getAmount().matches("\\d+(\\.\\d+)?");
        } catch (JsonSyntaxException | UnknownCurrencyException ex) {
            LOGGER.error(ex.getMessage());
            return false;
        }
    }

    private boolean processTransaction(TransactionDto currentTransaction, AccountDto currentAccount, String currentBalanceAfterTransaction) throws SQLException {
        boolean rsTransaction = this.transactionStorageManager.createTransaction(this.client,
                currentTransaction.getId(),
                currentTransaction.getMessageId(),
                currentTransaction.getAccountId(),
                currentTransaction.getAmount(),
                currentTransaction.getCurrency(),
                currentTransaction.getUpdateTime(),
                currentTransaction.getCreateTime(),
                currentTransaction.getEntryStatus(),
                currentTransaction.getStatus()
        );
        boolean rsAccount = this.accountStorageManager.updateAccount(this.client,
                currentAccount.getId(),
                currentAccount.getUserId(),
                currentBalanceAfterTransaction,
                currentAccount.getCreateTime(),
                String.valueOf(System.currentTimeMillis()),
                currentAccount.getCurrency()
        );
        return rsAccount  && rsTransaction;
    }

    private TransactionDto createTransactionDto(LoadTransactionRequest request, AccountDto currentAccount) {
        String currentTimestamp = String.valueOf(System.currentTimeMillis());
        return TransactionDto.builder()
                .id(UUID.randomUUID().toString())
                .accountId(currentAccount.getId())
                .messageId(request.getMessageId())
                .amount(request.getTransactionAmount().getAmount())
                .currency(request.getTransactionAmount().getCurrency())
                .entryStatus(request.getTransactionAmount().getDebitOrCredit())
                .createTime(currentTimestamp)
                .updateTime(currentTimestamp)
                .status(ResponseCode.APPROVED.code)
                .build();
    }

    private AccountDto getCurrentAccount(String userId) throws SQLException {
        if(this.accountStorageManager.getAccountCount(client, userId) == 0){
            return null;
        }
        Map<AccountResultSet, Object> account = this.accountStorageManager.getAccountById(this.client, userId,"");
        return AccountDto.builder()
                .id((String) account.get(AccountResultSet.ID))
                .userId((String) account.get(AccountResultSet.USER_ID))
                .balance((String) account.get(AccountResultSet.BALANCE))
                .createTime((String) account.get(AccountResultSet.CREATED_TIME))
                .updateTime((String) account.get(AccountResultSet.UPDATED_TIME))
                .currency((String) account.get(AccountResultSet.CURRENCY))
                .build();
    }

    private boolean isTransactionDuplicate(String messageId) throws SQLException {
        return this.transactionStorageManager.getTransactionCount(this.client, messageId) != 0;
    }
    private ActionResponse<LoadTransactionResponse> handleDuplicateTransaction(LoadTransactionRequest request, AccountDto currentAccount, String currentTimestamp) throws SQLException {
        String recentCreditOrDebit = this.transactionStorageManager.creditOrDebitStatus(this.client, currentAccount.getId());

        try {
            boolean rs = this.transactionStorageManager.createTransaction(this.client,
                    UUID.randomUUID().toString(),
                    request.getMessageId(),
                    currentAccount.getId(),
                    request.getTransactionAmount().getAmount(),
                    request.getTransactionAmount().getCurrency(),
                    currentTimestamp,
                    currentTimestamp,
                    request.getTransactionAmount().getDebitOrCredit(),
                    ResponseCode.DECLINED.code
            );

            if (rs) {
                client.commit();
                return constructResponse(
                        ActionType.LOAD.actionName,
                        ActionResponseStatus.CONFLICT,
                        "Message duplicated, please check your messageId",
                        LoadTransactionResponse.builder()
                                .userId(request.getUserId())
                                .messageId(request.getMessageId())
                                .balance(
                                        Balance.builder()
                                                .amount(currentAccount.getBalance())
                                                .currency(currentAccount.getCurrency())
                                                .debitOrCredit(recentCreditOrDebit)
                                                .build()
                                ).build(),
                        getActionName()
                );
            } else {
                client.rollback();
                return constructResponse(
                        ActionType.LOAD.actionName,
                        ActionResponseStatus.INTERNAL_SERVER_ERROR,
                        "Error occurred while processing request",
                        null,
                        getActionName()
                );
            }
        } catch (SQLException ex) {
            client.rollback();
            throw ex;
        } finally {
            client.close();
        }
    }
}
