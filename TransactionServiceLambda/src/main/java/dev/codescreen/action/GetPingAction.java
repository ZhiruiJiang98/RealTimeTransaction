package dev.codescreen.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import dev.codescreen.library.model.constant.ActionResponseStatus;
import dev.codescreen.library.model.server.ActionResponse;
import dev.codescreen.library.model.server.LoadTransactionResponse;

import java.io.IOException;
import java.sql.SQLException;

public class GetPingAction implements AbstractAction<APIGatewayProxyRequestEvent, String>{
    @Override
    public String getActionName() {
        return "GetPingAction";
    }

    @Override
    public ActionResponse<String> processRequest(APIGatewayProxyRequestEvent event) {
        return constructResponse(
                ActionResponseStatus.OK,
                "",
                "Server Ready to go!",
                getActionName()
        );
    }

    @Override
    public boolean requestValidated(APIGatewayProxyRequestEvent event) {
        return false;
    }
}
