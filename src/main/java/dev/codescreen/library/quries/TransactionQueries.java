package dev.codescreen.library.quries;

import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.model.constant.TransactionResultSet;

import java.util.Map;

public class TransactionQueries {
    public static String getTransactionCount(String messageId){
        return "SELECT COUNT(*) FROM Transaction WHERE messageId='" + messageId + "'";
    }
    public static String getTransaction(){
        return "SELECT * FROM Transaction LIMIT 10 OFFSET 0";
    }

    public static String createTransaction(String id, String accountId, String messageId, String transactionType, String amount, String currency, String updateTime, String createdTime, String debitOrCredit, String transactionStatus){
        return "INSERT INTO Transaction (id, accountId, messageId, transactionType, amount, currency, updateTime, createdTime, debitOrCredit, transactionStatus) VALUES ('" +
                id + "', '" + accountId + "', '" + messageId + "','" + transactionType + "', '" + amount + "', '" + currency + "', '" + updateTime + "', '" + createdTime + "', '" + debitOrCredit + "', '" + transactionStatus + "')";
    }

}
