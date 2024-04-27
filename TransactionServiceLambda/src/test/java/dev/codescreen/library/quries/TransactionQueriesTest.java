package dev.codescreen.library.quries;

import dev.codescreen.library.quries.TransactionQueries;
import org.junit.Test;
import dev.codescreen.library.model.constant.TransactionResultSet;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TransactionQueriesTest {
    private static final String TEST_ID = "testId";
    private static final String TEST_ACCOUNT_ID = "testAccountId";
    private static final String TEST_MESSAGE_ID = "messageId"; // "messageId" is not used in the createTransaction method, it should be "transactionType
    private static final String TEST_TRANSACTION_TYPE = "LOAD";
    private static final String TEST_AMOUNT = "1000.00";
    private static final String TEST_CURRENCY = "USD";
    private static final String TEST_UPDATE_TIME = "2021-08-01T00:00:00Z";
    private static final String TEST_CREATED_TIME = "2021-08-01T00:00:00Z";
    private static final String TEST_ENTRY_STATUS = "DEBIT";
    private static final String TEST_STATUS = "PENDING";

    private static final String EXPECTED_CREATE_TRANSACTION =
            "INSERT INTO Transaction (id, accountId, messageId, transactionType, amount, currency, updateTime, createdTime, debitOrCredit, transactionStatus) VALUES ('testId', 'testAccountId', 'messageId','LOAD', '1000.00', 'USD', '2021-08-01T00:00:00Z', '2021-08-01T00:00:00Z', 'DEBIT', 'PENDING')";
    private static final String EXPECTED_GET_TRANSACTION_COUNT = "SELECT COUNT(*) FROM Transaction WHERE messageId='messageId'";

    private static final Map<TransactionResultSet, Object> UPDATE_FIELDS = Map.of(
            TransactionResultSet.ACCOUNT_ID, TEST_ACCOUNT_ID,
            TransactionResultSet.TRANSACTION_TYPE, TEST_TRANSACTION_TYPE,
            TransactionResultSet.AMOUNT, TEST_AMOUNT,
            TransactionResultSet.CURRENCY, TEST_CURRENCY,
            TransactionResultSet.UPDATE_TIME, TEST_UPDATE_TIME,
            TransactionResultSet.CREATED_TIME, TEST_CREATED_TIME,
            TransactionResultSet.ENTRY_STATUS, TEST_ENTRY_STATUS,
            TransactionResultSet.STATUS, TEST_STATUS
    );
    @Test
    public  void test_create_transaction(){
        assertEquals(EXPECTED_CREATE_TRANSACTION, TransactionQueries.createTransaction(TEST_ID, TEST_ACCOUNT_ID,TEST_MESSAGE_ID, TEST_TRANSACTION_TYPE, TEST_AMOUNT, TEST_CURRENCY, TEST_UPDATE_TIME, TEST_CREATED_TIME, TEST_ENTRY_STATUS, TEST_STATUS));
    }

    @Test
    public void test_get_transaction_count(){
        assertEquals(EXPECTED_GET_TRANSACTION_COUNT, TransactionQueries.getTransactionCount(TEST_MESSAGE_ID));
    }
}
