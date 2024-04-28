package dev.codescreen.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import dev.codescreen.action.CreateAuthorizationTransactionAction;
import dev.codescreen.module.TransactionServiceModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import java.sql.SQLException;

public class CreateAuthorizationTransactionHandler implements AbstractHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{
    private static final Logger LOGGER = LogManager.getLogger(CreateAuthorizationTransactionHandler.class);
    private static final CreateAuthorizationTransactionAction action =
            Guice.createInjector(new TransactionServiceModule()).getInstance(CreateAuthorizationTransactionAction.class);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LambdaLogger logger = context.getLogger();
        logger.log("Request Context: " + gson.toJson(context));
        logger.log("Request Event: " + gson.toJson(event));

        try{
            System.out.println("Processing CreateAuthorizationTransactionHandler request ......");
            return processActionResponse(LOGGER, action.processRequest(event));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
