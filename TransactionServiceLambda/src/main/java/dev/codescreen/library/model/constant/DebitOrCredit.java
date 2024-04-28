package dev.codescreen.library.model.constant;

public enum DebitOrCredit {
    CREDIT("credit"),
    DEBIT("debit");
    public final String type;
    private DebitOrCredit(String type) {this.type = type;}
}
