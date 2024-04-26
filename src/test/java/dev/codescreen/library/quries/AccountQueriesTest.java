package dev.codescreen.library.quries;

import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.quries.AccountQueries;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AccountQueriesTest {

    private static final String EXPECTED_GET_ACCOUNT_COUNT_QUERY = "SELECT COUNT(*) FROM Account";
    private static final String USER_ID = "user123";
    private static final String ACCOUNT_ID = "account456";
    private static final String ACCOUNT_BALANCE = "1000.0";
    private static final String EXPECTED_UPDATE_ACCOUNT_QUERY = "UPDATE Account SET balance='1000.0' WHERE userId='" + USER_ID + "' AND accountId='" + ACCOUNT_ID +"'";

    @Test
    public void testGetAccountCount() {
        String actualQuery = AccountQueries.getAccountCount();
        assertEquals(EXPECTED_GET_ACCOUNT_COUNT_QUERY, actualQuery);
    }

    @Test
    public void testUpdateAccountById() {
        Map<AccountResultSet, Object> attributes = new HashMap<>();
        attributes.put(AccountResultSet.BALANCE, ACCOUNT_BALANCE);

        String actualQuery = AccountQueries.updateAccountById(USER_ID, ACCOUNT_ID, attributes);

        assertEquals(EXPECTED_UPDATE_ACCOUNT_QUERY, actualQuery);
    }
}