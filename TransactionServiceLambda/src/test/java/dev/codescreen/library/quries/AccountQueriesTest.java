package dev.codescreen.library.quries;

import dev.codescreen.library.model.constant.AccountResultSet;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AccountQueriesTest {


    private static final String TEST_USER_ID = "user123";
    private static final String TEST_ACCOUNT_ID = "account456";
    private static final String TEST_ACCOUNT_BALANCE = "1000.0";
    private static final String EXPECTED_UPDATE_ACCOUNT_QUERY = "UPDATE Account SET balance='1000.0' WHERE userId='" + TEST_USER_ID + "' AND accountId='" + TEST_ACCOUNT_ID +"'";
    private static final String EXPECTED_GET_ACCOUNT_COUNT_QUERY = String.format("SELECT COUNT(id) FROM Account WHERE userId = '%s'", TEST_USER_ID);
    @Test
    public void testGetAccountCount() {
        String actualQuery = AccountQueries.getAccountCount(TEST_USER_ID);
        assertEquals(EXPECTED_GET_ACCOUNT_COUNT_QUERY, actualQuery);
    }

    @Test
    public void testUpdateAccountById() {
        Map<AccountResultSet, Object> attributes = new HashMap<>();
        attributes.put(AccountResultSet.BALANCE, TEST_ACCOUNT_BALANCE);

        String actualQuery = AccountQueries.updateAccountById(TEST_USER_ID, TEST_ACCOUNT_ID, attributes);

        assertEquals(EXPECTED_UPDATE_ACCOUNT_QUERY, actualQuery);
    }
}