package dev.codescreen.action;

import dev.codescreen.library.model.constant.ActionResponseStatus;
import dev.codescreen.library.model.server.ActionResponse;

import java.io.IOException;
import java.sql.SQLException;

public interface AbstractAction<I, O> {
    public String getActionName();
    public ActionResponse<O> processRequest(I event) throws SQLException, IOException, NoSuchFieldException, IllegalAccessException;
    public boolean requestValidated(I event);

    default ActionResponse<O> constructResponse(
            String ActionType,
            ActionResponseStatus actionResponseStatus,
            String errorMessage,
            O data,
            String actionName
    ) {
        if(actionResponseStatus.equals(ActionResponseStatus.BAD_REQUEST) ||
                actionResponseStatus.equals(ActionResponseStatus.INTERNAL_SERVER_ERROR) ||
                        actionResponseStatus.equals(ActionResponseStatus.NOT_FOUND) ||
                actionResponseStatus.equals(ActionResponseStatus.UNAUTHORIZED) ||
                                actionResponseStatus.equals(ActionResponseStatus.FORBIDDEN) ||
                                        actionResponseStatus.equals(ActionResponseStatus.CONFLICT)){
            return ActionResponse.<O>builder()
                    .message(errorMessage)
                    .actionResponseStatus(actionResponseStatus)
                    .code(actionResponseStatus.statusCode.toString())
                    .build();
        }
        if(ActionType.equals("ping")){
            return ActionResponse.<O>builder()
                    .serverTime((String)data)
                    .actionResponseStatus(actionResponseStatus)
                    .build();
        }
        return ActionResponse.<O>builder()
                .data(data)
                .actionResponseStatus(actionResponseStatus)
                .message(errorMessage)
                .code(actionResponseStatus.statusCode.toString())
                .build();
    }
}
