package dev.codescreen.library.storage;


import dev.codescreen.library.mysql.MysqlClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AccountStorageManagerTest {
    private static final String ACCOUNT_ID = "account123";
    private static final String USER_ID = "user456";
    private static final String BALANCE = "1000";
    private static final String CREATED_TIME = "2023-06-08 10:00:00";
    private static final String LAST_UPDATED = "2023-06-08 11:00:00";
    private static final String CURRENCY = "USD";

    @Mock
    private MysqlClient mysqlClient;

    @InjectMocks
    private AccountStorageManager accountStorageManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateAccount_Success() throws SQLException {
        when(mysqlClient.executeUpdate(anyString())).thenReturn(1);

        boolean result = accountStorageManager.updateAccount(mysqlClient, ACCOUNT_ID, USER_ID, BALANCE,
                CREATED_TIME, LAST_UPDATED, CURRENCY);

        assertTrue(result);
        verify(mysqlClient, times(1)).executeUpdate(anyString());
    }

    @Test
    public void testUpdateAccount_Failure() throws SQLException {
        when(mysqlClient.executeUpdate(anyString())).thenReturn(0);

        boolean result = accountStorageManager.updateAccount(mysqlClient, ACCOUNT_ID, USER_ID, BALANCE,
                CREATED_TIME, LAST_UPDATED, CURRENCY);

        assertFalse(result);
    }

    @Test
    public void testUpdateAccount_SQLException() throws SQLException {
        when(mysqlClient.executeUpdate(anyString())).thenThrow(new SQLException("Database error"));

        try {
            accountStorageManager.updateAccount(mysqlClient, ACCOUNT_ID, USER_ID, BALANCE,
                    CREATED_TIME, LAST_UPDATED, CURRENCY);
        } catch (SQLException ex) {
            assertEquals("Database error", ex.getMessage());
        }

        verify(mysqlClient, times(1)).executeUpdate(anyString());
    }
}