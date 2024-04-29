package dev.codescreen.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.model.constant.ActionResponseStatus;
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


public class CreateAuthorizationTransactionAction implements AbstractAction<APIGatewayProxyRequestEvent, AuthorizationTransactionResponse> {
    private final Logger LOGGER = LogManager.getLogger(CreateAuthorizationTransactionAction.class);
    private final String host;
    private final String user;
    private final String pwd;
    private final MysqlClient client;
    private final TransactionStorageManager transactionStorageManager;
    private final AccountStorageManager accountStorageManager;

    @Inject
    public CreateAuthorizationTransactionAction(TransactionStorageManager transactionStorageManager,
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
        return "CreateTransactionAction";
    }

    @Override
    public ActionResponse<AuthorizationTransactionResponse> processRequest(APIGatewayProxyRequestEvent event) throws SQLException {
        if (!requestValidated(event)) {
            return constructResponse(
                    ActionResponseStatus.BAD_REQUEST,
                    "query parameter missing or data is not valid...",
                    null,
                    getActionName()
            );
        }
        try {
            LOGGER.info("Starting CreateAuthorizationTransactionAction ...");
            Gson gson = new Gson();
            this.client.connect(this.host, this.user, this.pwd);
            this.client.setAutoCommit(false);

            AuthorizationTransactionRequest request = gson.fromJson(event.getBody(), AuthorizationTransactionRequest.class);
            String currentTimestamp = String.valueOf(System.currentTimeMillis());

            //Get the current account
            AccountDto currentAccount = getCurrentAccount(request.getUserId());

            if (currentAccount == null) {
                return constructResponse(
                        ActionResponseStatus.NOT_FOUND,
                        "Account not found...",
                        null,
                        getActionName()
                );
            }
            //Check if transaction already exist
            if (isTransactionDuplicate(request.getMessageId())) {
                LOGGER.info("Transaction has already been debited or credited");
                return handleDuplicateTransaction(request, currentAccount, currentTimestamp);
            }

            TransactionDto currentTransaction = createTransactionDto(request, currentAccount);


            CurrencyExchanger currencyExchanger = new CurrencyExchanger();
            BigDecimal currentBalance = new BigDecimal(currentAccount.getBalance());
            MonetaryAmount exchangeAmount = currencyExchanger.exchange(
                    currentAccount.getCurrency(),
                    request.getTransactionAmount().getCurrency(),
                    request.getTransactionAmount().getAmount());
            BigDecimal currentTransactionAmount = exchangeAmount.getNumber().numberValue(BigDecimal.class);
            if (isInsufficientBalance(currentAccount.getBalance(), currentTransactionAmount)) {
                LOGGER.info("Insufficient balance");
                currentTransaction.setStatus(ResponseCode.DECLINED.code);
                return handleInsufficientBalance(request, currentTransaction, currentAccount);
            }


            String currentBalanceAfterTransaction = currentBalance.subtract(currentTransactionAmount).setScale(2, RoundingMode.CEILING).toString();
            currentTransaction.setStatus(ResponseCode.APPROVED.code);
            if (processTransaction(currentTransaction, currentAccount, currentBalanceAfterTransaction)) {

                client.commit();
                client.close();
                return constructResponse(
                        ActionResponseStatus.OK,
                        "Transaction created successfully...",
                        buildAuthorizationTransactionResponse(request, currentBalanceAfterTransaction, currentAccount.getCurrency()),
                        getActionName()
                );
            } else {

                return constructResponse(
                        ActionResponseStatus.BAD_REQUEST,
                        "Error occurred while processing request...",
                        null,
                        getActionName()
                );
            }

        } catch (SQLException ex) {
            client.rollback();
            client.close();
            LOGGER.error(ex.getMessage());
            return constructResponse(
                    ActionResponseStatus.BAD_REQUEST,
                    "Error occurred while processing request...",
                    null,
                    getActionName()
            );
        }


    }

    private AccountDto getCurrentAccount(String userId) throws SQLException {
        if (this.accountStorageManager.getAccountCount(client, userId) == 0) {
            return null;
        }
        Map<AccountResultSet, Object> account = this.accountStorageManager.getAccountById(this.client, userId, "");
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


    private ActionResponse<AuthorizationTransactionResponse> handleDuplicateTransaction(AuthorizationTransactionRequest request, AccountDto currentAccount, String currentTimestamp) throws SQLException {
        // ... (code for handling duplicate transactions)
        // if it is exist, then this transaction can't be processed. Return a declined response
        String recentCreditOrDebit = this.transactionStorageManager.creditOrDebitStatus(this.client, currentAccount.getId());
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
        // if the transaction is not created, then rollback the transaction and return an internal server error
        if (rs) {

            client.close();
            return constructResponse(
                    ActionResponseStatus.BAD_REQUEST,
                    "message duplicated, please check your messageId....",
                    null,
                    getActionName()
            );
        }

        client.close();

        // If thea transaction already exist, we will return a declined response
        return constructResponse(
                ActionResponseStatus.BAD_REQUEST,
                "Transaction already exist...",
                AuthorizationTransactionResponse.builder()
                        .userId(request.getUserId())
                        .messageId(request.getMessageId())
                        .responseCode(ResponseCode.DECLINED.code)
                        .balance(
                                Balance.builder()
                                        .amount(currentAccount.getBalance())
                                        .currency(currentAccount.getCurrency())
                                        .debitOrCredit(recentCreditOrDebit)
                                        .build()
                        ).build(),
                getActionName()
        );
    }

    private TransactionDto createTransactionDto(AuthorizationTransactionRequest request, AccountDto currentAccount) {
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
                .status(null)
                .build();
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
        return rsAccount && rsTransaction;
    }

    private AuthorizationTransactionResponse buildAuthorizationTransactionResponse(AuthorizationTransactionRequest request, String currentBalanceAfterTransaction, String currency) {
        return AuthorizationTransactionResponse.builder()
                .userId(request.getUserId())
                .messageId(request.getMessageId())
                .responseCode(ResponseCode.APPROVED.code)
                .balance(Balance.builder()
                        .amount(currentBalanceAfterTransaction)
                        .currency(currency)
                        .debitOrCredit(request.getTransactionAmount().getDebitOrCredit())
                        .build())
                .build();
    }

    private ActionResponse<AuthorizationTransactionResponse> handleInsufficientBalance(AuthorizationTransactionRequest request, TransactionDto currentTransaction, AccountDto currentAccount) throws SQLException {

        String recentCreditOrDebit = this.transactionStorageManager.creditOrDebitStatus(this.client, currentAccount.getId());
        currentTransaction.setStatus(ResponseCode.DECLINED.code);
        if (processTransaction(currentTransaction, currentAccount, currentAccount.getBalance())) {
            this.client.commit();
            this.client.close();
            return constructResponse(
                    ActionResponseStatus.BAD_REQUEST,
                    "Insufficient balance...",
                    AuthorizationTransactionResponse.builder()
                            .userId(request.getUserId())
                            .messageId(request.getMessageId())
                            .responseCode(ResponseCode.DECLINED.code)
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
            this.client.close();
            return constructResponse(
                    ActionResponseStatus.BAD_REQUEST,
                    "Error occurred while processing request...",
                    null,
                    getActionName()
            );
        }
    }

    private boolean isInsufficientBalance(String currentBalance, BigDecimal currentTransactionAmount) {
        return new BigDecimal(currentBalance).compareTo(currentTransactionAmount) < 0;
    }

    @Override
    public boolean requestValidated(APIGatewayProxyRequestEvent event) {
        LOGGER.info("Validating request...");

        if (event.getPathParameters() == null || event.getBody() == null || event.getPathParameters().isEmpty() || event.getBody().isEmpty()) {
            return false;
        }
        try {
            Gson gson = new Gson();
            AuthorizationTransactionRequest request = gson.fromJson(event.getBody(), AuthorizationTransactionRequest.class);
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
}
