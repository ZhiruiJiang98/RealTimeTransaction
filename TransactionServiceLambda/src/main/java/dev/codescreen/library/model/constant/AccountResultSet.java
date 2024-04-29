package dev.codescreen.library.model.constant;

import com.google.gson.annotations.SerializedName;
public enum AccountResultSet {
    @SerializedName("id")
    ID("id", "string"),
    @SerializedName("userId")
    USER_ID("userId", "string"),
    @SerializedName("balance")
    BALANCE("balance", "string"),
    @SerializedName("createdTime")
    CREATED_TIME("createdTime", "string"),
    @SerializedName("updatedTime")
    UPDATED_TIME("updatedTime", "string"),
    @SerializedName("currency")
    CURRENCY("currency", "string");
    public final String name;
    public final String type;
    private AccountResultSet(String name, String type) {this.name = name; this.type = type;}
}
