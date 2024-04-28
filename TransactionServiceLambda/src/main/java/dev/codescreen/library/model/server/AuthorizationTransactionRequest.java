package dev.codescreen.library.model.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizationTransactionRequest {
    private String messageId;
    private String userId;
    private TransactionAmount transactionAmount;
    public boolean syntacticallyValid() {
        return messageId != null && userId != null && transactionAmount != null;
    }
}
