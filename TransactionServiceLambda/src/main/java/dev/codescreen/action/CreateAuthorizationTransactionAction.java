package dev.codescreen.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import dev.codescreen.library.model.server.ActionResponse;

import java.io.IOException;
import java.sql.SQLException;

public class CreateAuthorizationTransactionAction implements AbstractAction<APIGatewayProxyRequestEvent, Boolean>{
    @Override
    public String getActionName() {
        return "CreateTransactionAction";
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
