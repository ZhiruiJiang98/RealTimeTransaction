package dev.codescreen.library.quries;

import dev.codescreen.library.model.constant.AccountResultSet;

import java.util.Map;
import java.util.stream.Collectors;

public class AccountQueries {
    public static String getAccountCount() {
        return "SELECT COUNT(*) FROM Account";
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
}
