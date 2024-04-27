package dev.codescreen.library.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionDto {
    private String id;
    private String accountId;
    private String transactionType;
    private String amount;
    private String currency;
    private String createTime;
    private String updateTime;
    private String status;
    private String entryStatus;
}
