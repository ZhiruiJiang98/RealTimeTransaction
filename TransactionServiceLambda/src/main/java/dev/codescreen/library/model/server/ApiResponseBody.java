package dev.codescreen.library.model.server;

import dev.codescreen.library.model.constant.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponseBody <T>{
    private Status status;
    private T data;
    private String errorMessage;
    private String actionName;
}
