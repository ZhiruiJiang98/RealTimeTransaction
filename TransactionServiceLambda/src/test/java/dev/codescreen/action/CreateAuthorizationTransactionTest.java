package dev.codescreen.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.model.constant.ActionResponseStatus;
import dev.codescreen.library.model.constant.ResponseCode;
import dev.codescreen.library.model.dto.AccountDto;
import dev.codescreen.library.model.server.*;
import dev.codescreen.library.mysql.MysqlClient;
import dev.codescreen.library.storage.AccountStorageManager;
import dev.codescreen.library.storage.TransactionStorageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CreateAuthorizationTransactionTest {

    private static final String USER_ID = "8786e2f9-d472-46a8-958f-d659880e723d";
    private static final String MESSAGE_ID = "50e70c62-e480-49fc-bc1b-e991ac672173";
    private static final String AMOUNT = "9000";
    private static final String CURRENCY = "USD";
    private static final String DEBIT_OR_CREDIT = "DEBIT";
    private static final String REQUEST_BODY = "{\"userId\":\"8786e2f9-d472-46a8-958f-d659880e723d\",\"messageId\":\"50e70c62-e480-49fc-bc1b-e991ac672173\",\"transactionAmount\":{\"amount\":\"9000\",\"currency\":\"USD\",\"debitOrCredit\":\"DEBIT\"}}";

    @Mock
    private TransactionStorageManager transactionStorageManager;

    @Mock
    private AccountStorageManager accountStorageManager;

    @Mock
    private MysqlClient mysqlClient;

    @Mock
    private CurrencyExchanger currencyExchanger;

    @InjectMocks
    private CreateAuthorizationTransactionAction createAuthorizationTransactionAction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processRequest_ValidRequest_Success() throws SQLException {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody(REQUEST_BODY);
        event.setPathParameters(Map.of("messageId", MESSAGE_ID));

        AccountDto accountDto = getAccountDto();
        Map<AccountResultSet, Object> accountResultSet = getAccountResultSet(accountDto);

        when(accountStorageManager.getAccountById(any(MysqlClient.class), eq(USER_ID), anyString()))
                .thenReturn(accountResultSet);
        when(transactionStorageManager.getTransactionCount(any(MysqlClient.class), eq(MESSAGE_ID))).thenReturn(0);
        when(transactionStorageManager.createTransaction(any(MysqlClient.class), anyString(), eq(MESSAGE_ID),
                anyString(), eq(AMOUNT), eq(CURRENCY), anyString(), anyString(), eq(DEBIT_OR_CREDIT), eq(ResponseCode.APPROVED.code)))
                .thenReturn(true);
        when(accountStorageManager.updateAccount(any(MysqlClient.class), anyString(), eq(USER_ID), anyString(),
                anyString(), anyString(), eq(CURRENCY))).thenReturn(true);
        when(accountStorageManager.getAccountCount(any(MysqlClient.class), eq(USER_ID))).thenReturn(1);

        // Act
        ActionResponse<AuthorizationTransactionResponse> response = createAuthorizationTransactionAction.processRequest(event);

        // Assert
        assertEquals(ActionResponseStatus.CREATED, response.getActionResponseStatus());
        assertEquals("1000.00", response.getData().getBalance().getAmount());
        assertEquals(CURRENCY, response.getData().getBalance().getCurrency());
        assertEquals(DEBIT_OR_CREDIT, response.getData().getBalance().getDebitOrCredit());
    }

    @Test
    void requestValidated_ValidRequest() {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody(REQUEST_BODY);
        event.setPathParameters(Map.of("messageId", MESSAGE_ID));

        // Act
        boolean isValid = createAuthorizationTransactionAction.requestValidated(event);

        // Assert
        assertEquals(true, isValid);
    }

    @Test
    void requestValidated_InvalidRequest_MissingFields() {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("{\"userId\":\"8786e2f9-d472-46a8-958f-d659880e723d\"}");

        // Act
        boolean isValid = createAuthorizationTransactionAction.requestValidated(event);

        // Assert
        assertEquals(false, isValid);
    }

    @Test
    void requestValidated_InvalidRequest_EmptyBody() {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("");

        // Act
        boolean isValid = createAuthorizationTransactionAction.requestValidated(event);

        // Assert
        assertEquals(false, isValid);
    }

    @Test
    void processRequest_UserNotFound() throws SQLException {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody(REQUEST_BODY);
        event.setPathParameters(Map.of("messageId", MESSAGE_ID));

        when(accountStorageManager.getAccountCount(any(MysqlClient.class), eq(USER_ID))).thenReturn(0);

        // Act
        ActionResponse<AuthorizationTransactionResponse> response = createAuthorizationTransactionAction.processRequest(event);

        // Assert
        assertEquals(ActionResponseStatus.NOT_FOUND, response.getActionResponseStatus());
        assertEquals("User not found...", response.getMessage());
    }

    @Test
    void processRequest_InsufficientBalance() throws SQLException {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody(REQUEST_BODY);
        event.setPathParameters(Map.of("messageId", MESSAGE_ID));

        AccountDto accountDto = getAccountDto();
        accountDto.setBalance("5000.00"); // Insufficient balance
        Map<AccountResultSet, Object> accountResultSet = getAccountResultSet(accountDto);

        when(accountStorageManager.getAccountById(any(MysqlClient.class), eq(USER_ID), anyString()))
                .thenReturn(accountResultSet);
        when(transactionStorageManager.getTransactionCount(any(MysqlClient.class), eq(MESSAGE_ID))).thenReturn(0);
        when(accountStorageManager.getAccountCount(any(MysqlClient.class), eq(USER_ID))).thenReturn(1);
        when(transactionStorageManager.createTransaction(any(MysqlClient.class), anyString(), eq(MESSAGE_ID),
                anyString(), eq(AMOUNT), eq(CURRENCY), anyString(), anyString(), eq(DEBIT_OR_CREDIT), eq(ResponseCode.DECLINED.code)))
                .thenReturn(true);
        when(accountStorageManager.updateAccount(any(MysqlClient.class), anyString(), eq(USER_ID), anyString(),
                anyString(), anyString(), eq(CURRENCY))).thenReturn(true);

        // Act
        ActionResponse<AuthorizationTransactionResponse> response = createAuthorizationTransactionAction.processRequest(event);

        // Assert
        assertEquals(ActionResponseStatus.PAYMENT_REQUIRED, response.getActionResponseStatus());

    }

    @Test
    void processRequest_DuplicateTransaction() throws SQLException {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody(REQUEST_BODY);
        event.setPathParameters(Map.of("messageId", MESSAGE_ID));

        AccountDto accountDto = getAccountDto();
        Map<AccountResultSet, Object> accountResultSet = getAccountResultSet(accountDto);

        when(accountStorageManager.getAccountById(any(MysqlClient.class), eq(USER_ID), anyString()))
                .thenReturn(accountResultSet);
        when(transactionStorageManager.getTransactionCount(any(MysqlClient.class), eq(MESSAGE_ID))).thenReturn(1);
        when(transactionStorageManager.creditOrDebitStatus(any(MysqlClient.class), anyString()))
                .thenReturn(DEBIT_OR_CREDIT);
        when(transactionStorageManager.createTransaction(any(MysqlClient.class), anyString(), eq(MESSAGE_ID),
                anyString(), eq(AMOUNT), eq(CURRENCY), anyString(), anyString(), eq(DEBIT_OR_CREDIT), eq(ResponseCode.DECLINED.code)))
                .thenReturn(true);
        when(accountStorageManager.getAccountCount(any(MysqlClient.class), eq(USER_ID))).thenReturn(1);

        // Act
        ActionResponse<AuthorizationTransactionResponse> response = createAuthorizationTransactionAction.processRequest(event);

        // Assert
        assertEquals(ActionResponseStatus.CONFLICT, response.getActionResponseStatus());
    }

    private Map<AccountResultSet, Object> getAccountResultSet(AccountDto accountDto) {
        Map<AccountResultSet, Object> resultSet = new HashMap<>();
        resultSet.put(AccountResultSet.ID, accountDto.getId());
        resultSet.put(AccountResultSet.USER_ID, accountDto.getUserId());
        resultSet.put(AccountResultSet.BALANCE, accountDto.getBalance());
        resultSet.put(AccountResultSet.CREATED_TIME, accountDto.getCreateTime());
        resultSet.put(AccountResultSet.UPDATED_TIME, accountDto.getUpdateTime());
        resultSet.put(AccountResultSet.CURRENCY, accountDto.getCurrency());
        return resultSet;
    }
    private AccountDto getAccountDto() {
        return AccountDto.builder()
                .id("accountId")
                .userId(USER_ID)
                .balance("10000.00")
                .createTime("createTime")
                .updateTime("updateTime")
                .currency(CURRENCY)
                .build();
    }

}
