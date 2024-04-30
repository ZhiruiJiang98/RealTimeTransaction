package dev.codescreen.library.model.server;

import dev.codescreen.library.model.constant.ActionResponseStatus;
import dev.codescreen.library.model.constant.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActionResponse <T>{
    private ActionResponseStatus actionResponseStatus;
    private Status getResponseStatus;
    private String message;
    private String errorMessage;
    private T data;
    private String actionName;
    private String serverTime;
    private String code;
}
