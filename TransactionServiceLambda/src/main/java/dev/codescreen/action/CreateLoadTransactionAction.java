package dev.codescreen.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import dev.codescreen.library.model.server.ActionResponse;

import java.io.IOException;
import java.sql.SQLException;

public class CreateLoadTransactionAction implements AbstractAction<APIGatewayProxyRequestEvent, Boolean>{
    @Override
    public String getActionName() {
        return "";
    }

    @Override
    public ActionResponse<Boolean> processRequest(APIGatewayProxyRequestEvent event) throws SQLException{
        return null;
    }

    @Override
    public boolean requestValidated(APIGatewayProxyRequestEvent event) {
        return false;
    }
}
