package dev.codescreen.library.quries;

import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.model.constant.TransactionResultSet;

import java.util.Map;

public class TransactionQueries {
    public static String getTransactionCount(){
        return "SELECT COUNT(*) FROM Transaction";
    }
    public static String getTransaction(){
        return "SELECT * FROM Transaction LIMIT 10 OFFSET 0";
    }

    public static String createTransaction(String messageId, Map<TransactionResultSet, Object> attributes){
        return "INSERT INTO Transaction (id, accountId, transactionType, amount, currency, updateTime, createdTime, debitOrCredit, transactionStatus) VALUES ('" +
                messageId + "', '" +
                attributes.get(TransactionResultSet.ACCOUNT_ID) + "', '" +
                attributes.get(TransactionResultSet.TRANSACTION_TYPE) + "', '" +
                attributes.get(TransactionResultSet.AMOUNT) + "', '" +
                attributes.get(TransactionResultSet.CURRENCY) + "', '" +
                attributes.get(TransactionResultSet.UPDATE_TIME) + "', '" +
                attributes.get(TransactionResultSet.CREATED_TIME) + "', '" +
                attributes.get(TransactionResultSet.ENTRY_STATUS) + "', '" +
                attributes.get(TransactionResultSet.STATUS) + "')";
    }

}
