package dev.codescreen.library.storage;

import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.mysql.MysqlClient;
import dev.codescreen.library.quries.TransactionQueries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionStorageManager {
    private final Logger LOGGER = LogManager.getLogger(TransactionStorageManager.class);
    public TransactionStorageManager() {}
    public boolean createTransaction(MysqlClient client, String id, String messageId, String accountId, String amount, String currency, String updateTime, String createdTime, String debitOrCredit, String transactionStatus) throws SQLException {
        LOGGER.info(String.format("Creating Transaction with id: %s & messageId: %s", id, messageId));
        String query = TransactionQueries.createTransaction(id, accountId, messageId, amount, currency, updateTime, createdTime, debitOrCredit, transactionStatus);
        try{
            int rs = client.executeUpdate(query);
            return rs == 1;
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
            ResultSet rs = client.executeQuery(query);
            if(rs.next()){
                return rs.getInt(1);
            }
            return client.executeQuery(query).getInt(1);

        } catch (SQLException ex) {
            LOGGER.error("Error occurred while getting transaction count", ex);
            throw ex;
        }
    }
    public String creditOrDebitStatus(MysqlClient client, String accountId) throws SQLException{
        LOGGER.info("Getting credit or debit status");
        try{
            LOGGER.info("Executing creditOrDebitStatus query: " + TransactionQueries.creditOrDebitStatus(accountId));
            String query = TransactionQueries.creditOrDebitStatus(accountId);
            ResultSet rs = client.executeQuery(query);
            if(rs.next()){
                return rs.getString("debitOrCredit");
            }
            return null;
        } catch (SQLException ex) {
            LOGGER.error("Error occurred while getting credit or debit status", ex);
            throw ex;
        }
    }
}
