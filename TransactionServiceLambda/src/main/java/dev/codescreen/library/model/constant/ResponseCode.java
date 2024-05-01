package dev.codescreen.library.model.constant;

public enum ResponseCode {
    APPROVED("APPROVED"),
    DECLINED("DECLINED");
    public final String code;
    private ResponseCode(String code) {this.code = code;}
}
