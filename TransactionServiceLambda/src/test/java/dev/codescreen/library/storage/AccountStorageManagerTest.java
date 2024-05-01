package dev.codescreen.library.storage;


import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.mysql.MysqlClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

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

    @Mock
    private ResultSet resultSet;

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

        assertThrows(SQLException.class, () -> accountStorageManager.updateAccount(mysqlClient, ACCOUNT_ID, USER_ID, BALANCE,
                CREATED_TIME, LAST_UPDATED, CURRENCY));

        verify(mysqlClient, times(1)).executeUpdate(anyString());
    }

    @Test
    public void testGetAccountById_Success() throws SQLException {
        when(mysqlClient.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString(anyString())).thenReturn(ACCOUNT_ID);

        Map<AccountResultSet, Object> result = accountStorageManager.getAccountById(mysqlClient, USER_ID, ACCOUNT_ID);

        assertNotNull(result);
        assertEquals(ACCOUNT_ID, result.get(AccountResultSet.ID));
        verify(mysqlClient, times(1)).executeQuery(anyString());
    }

    @Test
    public void testGetAccountById_SQLException() throws SQLException {
        when(mysqlClient.executeQuery(anyString())).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> accountStorageManager.getAccountById(mysqlClient, USER_ID, ACCOUNT_ID));

        verify(mysqlClient, times(1)).executeQuery(anyString());
    }

    @Test
    public void testGetAccountCount_Success() throws SQLException {
        when(mysqlClient.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        int count = accountStorageManager.getAccountCount(mysqlClient, USER_ID);

        assertEquals(1, count);
        verify(mysqlClient, times(1)).executeQuery(anyString());
    }

    @Test
    public void testGetAccountCount_SQLException() throws SQLException {
        when(mysqlClient.executeQuery(anyString())).thenThrow(new SQLException("Database error"));

        assertThrows(RuntimeException.class, () -> accountStorageManager.getAccountCount(mysqlClient, USER_ID));

        verify(mysqlClient, times(1)).executeQuery(anyString());
    }
}