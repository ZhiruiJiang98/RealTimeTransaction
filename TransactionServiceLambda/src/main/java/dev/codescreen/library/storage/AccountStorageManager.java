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
    public boolean updateAccount(MysqlClient client, String id, String userId, String balance, String createdTime, String updatedTime, String currency) throws SQLException{
        LOGGER.info(String.format("Updating Account with id: %s", id));
        try{

            Map<AccountResultSet, Object> map = Map.of(
                AccountResultSet.USER_ID, userId,
                AccountResultSet.BALANCE, balance,
                AccountResultSet.CREATED_TIME, createdTime,
                AccountResultSet.UPDATED_TIME, updatedTime,
                AccountResultSet.CURRENCY, currency,
                AccountResultSet.ID, id
            );
            String query = AccountQueries.updateAccountById(userId, id, map);
            int rs  = client.executeUpdate(query);
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


               while(rs.next()){
                   for(AccountResultSet field: AccountResultSet.values()){
                       switch (field.type) {
                           case "string":
                               result.put(field, rs.getString(field.name));
                               break;
                           case "int":
                               result.put(field, rs.getInt(field.name));
                               break;
                       }
                   }
            }
            return result;
        } catch (SQLException ex) {
            LOGGER.error("Error occurred while getting account", ex);
            throw ex;
        }
    }
    public int getAccountCount(MysqlClient client, String userId){
        LOGGER.info("Getting Account count");
        try{
            LOGGER.info("Executing getAccountCount query: " + AccountQueries.getAccountCount(userId));
            String query = AccountQueries.getAccountCount(userId);
            ResultSet rs = client.executeQuery(query);
            if(rs.next()){
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException ex) {
            LOGGER.error("Error occurred while getting account count", ex);
            throw new RuntimeException(ex);
        }
    }
}
