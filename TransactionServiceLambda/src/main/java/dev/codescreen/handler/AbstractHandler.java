package dev.codescreen.handler;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import dev.codescreen.library.model.constant.ActionResponseStatus;
import dev.codescreen.library.model.server.ActionResponse;
import dev.codescreen.library.model.server.ApiResponseBody;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public interface AbstractHandler<I, O> extends RequestHandler<I, O> {
    default <O> APIGatewayProxyResponseEvent processActionResponse(Logger LOGGER, ActionResponse<O> actionResponse) {
        Gson gson = new Gson();
        LOGGER.info(String.format("Processing Action Response Action: %s", actionResponse.getActionName()));
        LOGGER.info(String.format("Action resulted in %s with error message: %s",
                actionResponse.getActionResponseStatus(), actionResponse.getErrorMessage()));
        APIGatewayProxyResponseEvent jsonResponse = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");

        jsonResponse.setStatusCode(actionResponse.getActionResponseStatus().statusCode);
        jsonResponse.setHeaders(headers);

        if (actionResponse.getActionResponseStatus().equals(ActionResponseStatus.OK) || actionResponse.getActionResponseStatus().equals(ActionResponseStatus.CREATED)) {
            if (actionResponse.getServerTime() == null) {
                jsonResponse.setBody(gson.toJson(actionResponse.getData()));
            } else {
                jsonResponse.setBody(gson.toJson(ApiResponseBody.<O>builder()
                        .serverTime(actionResponse.getServerTime()).build()));
            }
            return jsonResponse;
        }
        jsonResponse.setBody(gson.toJson(ApiResponseBody.<O>builder()
                .status(actionResponse.getGetResponseStatus())
                .data(actionResponse.getData())
                .message(actionResponse.getMessage())
                .code(actionResponse.getCode())
                .message(actionResponse.getMessage())
                .build()));

        return jsonResponse;
    }
}
