package dev.codescreen.library.storage;

import dev.codescreen.library.model.constant.AccountResultSet;
import dev.codescreen.library.mysql.MysqlClient;
import dev.codescreen.library.quries.AccountQueries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AccountStorageManager {
    private final Logger LOGGER = LogManager.getLogger(AccountStorageManager.class);
    public AccountStorageManager() {}
    public boolean updateAccount(MysqlClient client, String id, String userId, String balance, String createdTime, String lastUpdated, String currency) throws SQLException{
        LOGGER.info(String.format("Updating Account with id: %s", id));
        try{
            int rs  = client.executeUpdate(String.format("UPDATE Account SET userId='%s', balance='%s', createdTime='%s', lastUpdated='%s', currency='%s' WHERE id='%s'", userId, balance, createdTime, lastUpdated, currency, id));
            return rs == 1;
        } catch (SQLException ex) {
            LOGGER.error("Error occurred while updating account", ex);
            throw ex;
        }
    }
    public Map<AccountResultSet, Object> getAccountById(MysqlClient client, String userId, String accountId) throws SQLException{
        LOGGER.info(String.format("Getting Account with userId: %s", userId));
        try{
            ResultSet rs = client.executeQuery(AccountQueries.getAccountById(userId, accountId));
            Map<AccountResultSet, Object> result = new HashMap<>();
            if(rs.next()){
                result = Map.of(
                        AccountResultSet.ID, rs.getString("id"),
                        AccountResultSet.USER_ID, rs.getString("userId"),
                        AccountResultSet.BALANCE, rs.getString("balance"),
                        AccountResultSet.CREATED_TIME, rs.getString("createdTime"),
                        AccountResultSet.LAST_UPDATED, rs.getString("lastUpdated"),
                        AccountResultSet.CURRENCY, rs.getString("currency")
                );
            }
            return result;
        } catch (SQLException ex) {
            LOGGER.error("Error occurred while getting account", ex);
            throw ex;
        }
    }
}
