package dev.codescreen.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.model.constant.ActionResponseStatus;
import dev.codescreen.library.model.dto.AccountDto;
import dev.codescreen.library.model.server.ActionResponse;
import dev.codescreen.library.model.server.LoadTransactionResponse;
import dev.codescreen.library.mysql.MysqlClient;
import dev.codescreen.library.storage.AccountStorageManager;
import dev.codescreen.library.storage.TransactionStorageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CreateLoadTransactionActionTest {
    private static final String USER_ID = "2226e2f9-ih09-46a8-958f-d659880asdfD";
    private static final String MESSAGE_ID = "55210c62-e480-asdf-bc1b-e991ac67FSAC";
    private static final String AMOUNT = "100.23";
    private static final String CURRENCY = "USD";
    private static final String DEBIT_OR_CREDIT = "CREDIT";
    private static final String REQUEST_BODY = "{\"userId\":\"2226e2f9-ih09-46a8-958f-d659880asdfD\",\"messageId\":\"55210c62-e480-asdf-bc1b-e991ac67FSAC\",\"transactionAmount\":{\"amount\":\"100.23\",\"currency\":\"USD\",\"debitOrCredit\":\"CREDIT\"}}";
    @Mock
    private TransactionStorageManager transactionStorageManager;

    @Mock
    private AccountStorageManager accountStorageManager;

    @Mock
    private MysqlClient mysqlClient;

    @InjectMocks
    private CreateLoadTransactionAction createLoadTransactionAction;

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

        when(accountStorageManager.getAccountCount(any(MysqlClient.class), eq(USER_ID))).thenReturn(1);
        when(accountStorageManager.getAccountById(any(MysqlClient.class), eq(USER_ID), anyString()))
                .thenReturn(accountResultSet);
        when(transactionStorageManager.getTransactionCount(any(MysqlClient.class), eq(MESSAGE_ID))).thenReturn(0);
        when(transactionStorageManager.createTransaction(any(MysqlClient.class), anyString(), eq(MESSAGE_ID),
                anyString(), eq(AMOUNT), eq(CURRENCY), anyString(), anyString(), eq(DEBIT_OR_CREDIT), anyString()))
                .thenReturn(true);
        when(accountStorageManager.updateAccount(any(MysqlClient.class), anyString(), eq(USER_ID), anyString(),
                anyString(), anyString(), eq(CURRENCY))).thenReturn(true);

        // Act
        ActionResponse<LoadTransactionResponse> response = createLoadTransactionAction.processRequest(event);

        // Assert
        assertEquals(ActionResponseStatus.OK, response.getActionResponseStatus());
    }

    @Test
    void requestValidated_ValidRequest() {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody(REQUEST_BODY);
        event.setPathParameters(Map.of("messageId", MESSAGE_ID));

        // Act
        boolean isValid = createLoadTransactionAction.requestValidated(event);

        // Assert
        assertEquals(true, isValid);
    }

    @Test
    void requestValidated_InvalidRequest_MissingFields() {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("{\"userId\":\"2226e2f9-ih09-46a8-958f-d659880asdfD\"}");
        event.setPathParameters(Map.of("messageId", MESSAGE_ID));

        // Act
        boolean isValid = createLoadTransactionAction.requestValidated(event);

        // Assert
        assertEquals(false, isValid);
    }

    @Test
    void requestValidated_InvalidRequest_EmptyBody() {
        // Arrange
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("");
        event.setPathParameters(Map.of("messageId", MESSAGE_ID));

        // Act
        boolean isValid = createLoadTransactionAction.requestValidated(event);

        // Assert
        assertEquals(false, isValid);
    }

    private AccountDto getAccountDto() {
        return AccountDto.builder()
                .id("accountId")
                .userId(USER_ID)
                .balance("100.00")
                .createTime("createTime")
                .updateTime("updateTime")
                .currency(CURRENCY)
                .build();
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
}
