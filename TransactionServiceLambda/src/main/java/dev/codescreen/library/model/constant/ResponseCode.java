package dev.codescreen.library.model.constant;

public enum ResponseCode {
    APPROVED("approved"),
    DECLINED("declined");
    public final String code;
    private ResponseCode(String code) {this.code = code;}
}
