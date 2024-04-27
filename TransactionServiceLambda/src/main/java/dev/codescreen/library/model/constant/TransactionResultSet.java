package dev.codescreen.library.model.constant;

import com.google.gson.annotations.SerializedName;


public enum TransactionResultSet {
    @SerializedName("id")
    ID("id", "string"),
    @SerializedName("accountId")
    ACCOUNT_ID("accountId", "string"),
    @SerializedName("amount")
    AMOUNT("amount", "string"),
    @SerializedName("transactionType")
    TRANSACTION_TYPE("transactionType", "string"),
    @SerializedName("createdTime")
    CREATED_TIME("createdTime", "string"),
    @SerializedName("updateTime")
    UPDATE_TIME("updateTime", "string"),
    @SerializedName("currency")
    CURRENCY("currency", "string"),
    @SerializedName("status")
    STATUS("status", "string"),
    @SerializedName("entryStatus")
    ENTRY_STATUS("entryStatus", "string");
    public final String name;
    public final String type;
    private TransactionResultSet(String name, String type) {this.name = name; this.type = type;}

}
