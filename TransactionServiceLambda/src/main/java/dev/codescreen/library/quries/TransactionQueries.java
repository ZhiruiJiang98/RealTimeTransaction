package dev.codescreen.library.quries;

import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.model.constant.TransactionResultSet;

import java.util.Map;

public class TransactionQueries {
    public static String getTransactionCount(String messageId){
        return "SELECT COUNT(messageId) FROM Transaction WHERE messageId = '" + messageId + "'";
    }
    public static String getTransaction(){
        return "SELECT * FROM Transaction LIMIT 10 OFFSET 0";
    }

    public static String createTransaction(String id, String accountId, String messageId, String amount, String currency, String updatedTime, String createdTime, String debitOrCredit, String transactionStatus){
        return "INSERT INTO Transaction (id, accountId, messageId, amount, currency, updatedTime, createdTime, debitOrCredit, transactionStatus) VALUES ('" +
                id + "', '" + accountId + "', '" + messageId + "','" + amount + "', '" + currency + "', '" + updatedTime + "', '" + createdTime + "', '" + debitOrCredit + "', '" + transactionStatus + "')";
    }

    public static String creditOrDebitStatus(String accountId){
        return String.format("SELECT debitOrCredit FROM Transaction WHERE accountId = '%s' ORDER BY updatedTime DESC LIMIT 1", accountId);
    }

}
