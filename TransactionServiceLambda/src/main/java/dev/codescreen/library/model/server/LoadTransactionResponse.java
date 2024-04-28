package dev.codescreen.library.model.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoadTransactionResponse {
    private String  messageId;
    private String userId;
    private Balance balance;
}
