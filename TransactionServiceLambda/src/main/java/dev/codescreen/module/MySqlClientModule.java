package dev.codescreen.module;

import com.google.gson.Gson;
import com.google.inject.name.Names;
import dev.codescreen.library.model.server.DatabaseSecret;
import dev.codescreen.library.mysql.MysqlClient;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import com.google.inject.AbstractModule;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class MySqlClientModule extends AbstractModule  {
    private final Logger LOGGER = LogManager.getLogger(MySqlClientModule.class);
    @Override
    protected void configure() {
        LOGGER.info("Getting database credentials..");
        DatabaseSecret secret = getDbSecret();
        bindConstant().annotatedWith(Names.named("MysqlHost")).to(
                String.format("jdbc:mysql://%s/%s", secret.getHost(), System.getenv("LOCAL_MYSQL_DATABASE"))
        );
        bindConstant().annotatedWith(Names.named("MysqlUser")).to(secret.getUsername());
        bindConstant().annotatedWith(Names.named("MysqlPassword")).to(secret.getPassword());
        try{
            bind(MysqlClient.class).toInstance(new MysqlClient());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private DatabaseSecret getDbSecret(){

            return getLocalDbSecret();

    }
    private DatabaseSecret getLocalDbSecret(){
        String localHost = System.getenv("LOCAL_MYSQL_HOST");
        String localUsername = System.getenv("LOCAL_MYSQL_USERNAME");
        String localPassword = System.getenv("LOCAL_MYSQL_PASSWORD");
        return  DatabaseSecret.builder()
                .host(localHost)
                .username(localUsername)
                .password(localPassword)
                .build();
    }
    private DatabaseSecret getAWSRdsDbSecret() {
        String secretName = System.getenv("dbSecretName");
        Region region = Region.of("us-west-2");

        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            LOGGER.error(e.getMessage());
            throw e;
        }
        Gson gson = new Gson();
        return gson.fromJson(getSecretValueResponse.secretString(), DatabaseSecret.class);
    }
}
