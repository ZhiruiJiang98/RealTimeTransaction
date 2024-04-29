package dev.codescreen.library.quries;

import dev.codescreen.library.model.constant.AccountResultSet;

import java.util.Map;
import java.util.stream.Collectors;

public class AccountQueries {
    public static String getAccountCount(String userId) {
        return String.format("SELECT COUNT(id) FROM Account WHERE userId = '%s'", userId);
    }

    public static String updateAccountById(String userId, String accountId, Map<AccountResultSet, Object> attributes) {
        return "UPDATE Account SET " +
                attributes.keySet().stream().map(key -> {
                    String value = key.type.equals("string") ? "'" + attributes.get(key).toString() + "'"
                            : attributes.get(key).toString();
                    return key.name + "=" + value;
                }).collect(Collectors.joining(",")) +
                " WHERE userId='" + userId + "' AND accountId='" + accountId + "'";
    }
    public static String getAccountById(String userId, String accountId) {
        return "SELECT * FROM Account WHERE userId='" + userId + "';";
    }

}
