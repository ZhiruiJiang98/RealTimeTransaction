package dev.codescreen.library.storage;

import dev.codescreen.library.mysql.MysqlClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class AccountStorageManager {
    private final Logger LOGGER = LogManager.getLogger(AccountStorageManager.class);
    public AccountStorageManager() {}
    public String updateAccount(MysqlClient client, String id, String userId, String balance, String createdTime, String lastUpdated, String currency) throws SQLException{
        LOGGER.info(String.format("Updating Account with id: %s", id));
        try{
            int rs  = client.executeUpdate(String.format("UPDATE Account SET userId='%s', balance='%s', createdTime='%s', lastUpdated='%s', currency='%s' WHERE id='%s'", userId, balance, createdTime, lastUpdated, currency, id));
            return rs == 1 ? id : null;
        } catch (SQLException ex) {
            LOGGER.error("Error occurred while updating account", ex);
            throw ex;
        }
    }
}
