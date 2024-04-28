package dev.codescreen.library.model.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Balance {
    private String currency;
    private String amount;
    private String debitOrCredit;
}
