package dev.codescreen.library.model.constant;
public enum ActionResponseStatus {
    OK(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    public final Integer statusCode;

    private ActionResponseStatus(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
