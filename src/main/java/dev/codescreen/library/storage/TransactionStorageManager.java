package dev.codescreen.library.storage;

import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.mysql.MysqlClient;
import dev.codescreen.library.quries.TransactionQueries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TransactionStorageManager {
    private final Logger LOGGER = LogManager.getLogger(TransactionStorageManager.class);
    public TransactionStorageManager() {}
    public String createTransaction(MysqlClient client, String id, String messageId, String accountId, String transactionType, String amount, String currency, String updateTime, String createdTime, String debitOrCredit, String transactionStatus) throws SQLException {
        LOGGER.info(String.format("Creating Transaction with id: %s & messageId: %s", id, messageId));
        String query = TransactionQueries.createTransaction(id, accountId, messageId, transactionType, amount, currency, updateTime, createdTime, debitOrCredit, transactionStatus);
        try{
            int rs = client.executeUpdate(query);
            return rs == 1 ? id : null;
        } catch (SQLException ex) {
            LOGGER.error("Error occurred while creating transaction", ex);
            throw ex;
        }

    }
    public int getTransactionCount(MysqlClient client, String messageId) throws SQLException{
        LOGGER.info("Getting Transaction count");
        try{
            LOGGER.info("Executing getTransactionCount query: " + TransactionQueries.getTransactionCount(messageId));
            String query = TransactionQueries.getTransactionCount(messageId);
            return client.executeQuery(query).getInt(1);

        } catch (SQLException ex) {
            LOGGER.error("Error occurred while getting transaction count", ex);
            throw ex;
        }
    }
}
