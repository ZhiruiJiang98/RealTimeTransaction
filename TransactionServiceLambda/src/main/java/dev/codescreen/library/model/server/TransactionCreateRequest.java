package dev.codescreen.library.model.server;

import dev.codescreen.library.model.dto.TransactionDto;
import java.util.Map;

public class TransactionCreateRequest {
    private String id;
    private Map<TransactionDto, Object> attributes;
    public boolean syntacticValidated() {
        return this.id != null && this.attributes != null;
    }
}
