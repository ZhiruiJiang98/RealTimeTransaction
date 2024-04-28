package dev.codescreen.library.model.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionAmount {
    private String amount;
    private String currency;
    private String debitOrCredit;
}
