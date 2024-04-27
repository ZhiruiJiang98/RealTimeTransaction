package dev.codescreen.library.model.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatabaseSecret {
    private String username;
    private String password;
    private String engine;
    private String host;
    private String port;
    private String dbInstanceIdentifier;
}
