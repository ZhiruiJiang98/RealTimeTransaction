package dev.codescreen.library.storage;

import dev.codescreen.library.mysql.MysqlClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


public class TransactionStorageManagerTest {
    private static final String TRANSACTION_ID = "transaction123";
    private static final String MESSAGE_ID = "message456";
    private static final String ACCOUNT_ID = "account789";
    private static final String TRANSACTION_TYPE = "DEPOSIT";
    private static final String AMOUNT = "1000";
    private static final String CURRENCY = "USD";
    private static final String UPDATE_TIME = "2023-06-08 10:00:00";
    private static final String CREATED_TIME = "2023-06-08 10:00:00";
    private static final String DEBIT_OR_CREDIT = "CREDIT";
    private static final String TRANSACTION_STATUS = "COMPLETED";

    @Mock
    private MysqlClient mysqlClient;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private TransactionStorageManager transactionStorageManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testCreateTransaction() throws SQLException {
        when(mysqlClient.executeUpdate(anyString())).thenReturn(1);

        String result = transactionStorageManager.createTransaction(mysqlClient, TRANSACTION_ID, MESSAGE_ID, ACCOUNT_ID,
                TRANSACTION_TYPE, AMOUNT, CURRENCY, UPDATE_TIME, CREATED_TIME, DEBIT_OR_CREDIT, TRANSACTION_STATUS);

        assertEquals(TRANSACTION_ID, result);
    }

    @Test
    public void testCreateTransaction_Failure() throws SQLException {
        when(mysqlClient.executeUpdate(anyString())).thenReturn(0);

        String result = transactionStorageManager.createTransaction(mysqlClient, TRANSACTION_ID, MESSAGE_ID, ACCOUNT_ID,
                TRANSACTION_TYPE, AMOUNT, CURRENCY, UPDATE_TIME, CREATED_TIME, DEBIT_OR_CREDIT, TRANSACTION_STATUS);

        assertNull(result);
        verify(mysqlClient, times(1)).executeUpdate(anyString());
    }

    @Test
    public void testGetTransactionCount() throws SQLException {
        when(mysqlClient.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.getInt(1)).thenReturn(5);

        int count = transactionStorageManager.getTransactionCount(mysqlClient, MESSAGE_ID);

        assertEquals(5, count);
        verify(mysqlClient, times(1)).executeQuery(anyString());
        verify(resultSet, times(1)).getInt(1);
    }
}
