package dev.codescreen.library.model.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizationTransactionResponse {
    private String messageId;
    private String userId;
    private String responseCode;
    private Balance balance;
}
