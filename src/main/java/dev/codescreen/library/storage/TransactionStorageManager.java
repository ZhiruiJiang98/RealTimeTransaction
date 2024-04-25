package dev.codescreen.library.storage;

import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.mysql.MysqlClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TransactionStorageManager {
    private final Logger LOGGER = LogManager.getLogger(TransactionStorageManager.class);
    public TransactionStorageManager() {}
    public String createTransaction(MysqlClient client, String id, String accountId, String transactionType, String amount, String currency, String updateTime, String createdTime, String debitOrCredit, String transactionStatus) throws SQLException{
        LOGGER.info(String.format("Creating Transaction with id: %s", id));
        try{
            int rs  = client.executeUpdate(String.format("INSERT INTO Transaction (id, accountId, transactionType, amount, currency, updateTime, createdTime, debitOrCredit, transactionStatus) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", id, accountId, transactionType, amount, currency, updateTime, createdTime, debitOrCredit, transactionStatus));
            return rs == 1 ? id : null;
        } catch (SQLException ex) {
            LOGGER.error("Error occurred while creating transaction", ex);
            throw ex;
        }
    }
}
