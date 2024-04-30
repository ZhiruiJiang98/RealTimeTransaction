package dev.codescreen.action;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import dev.codescreen.library.model.constant.ActionResponseStatus;
import dev.codescreen.library.model.constant.ActionType;
import dev.codescreen.library.model.server.ActionResponse;
import dev.codescreen.library.model.server.LoadTransactionResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class GetPingAction implements AbstractAction<APIGatewayProxyRequestEvent, String>{
    @Override
    public String getActionName() {
        return "GetPingAction";
    }

    @Override
    public ActionResponse<String> processRequest(APIGatewayProxyRequestEvent event) {
        // Get the current timestamp
        Instant now = Instant.now();

        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .withZone(ZoneOffset.UTC);

        // Format the timestamp
        String serverTime = formatter.format(now);
        return constructResponse(
                ActionType.PING.actionName,
                ActionResponseStatus.OK,
                "",
                serverTime,
                getActionName()
        );
    }

    @Override
    public boolean requestValidated(APIGatewayProxyRequestEvent event) {
        return false;
    }
}
