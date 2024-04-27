package dev.codescreen.library.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccountDto {
    private String id;
    private String userId;
    private String balance;
    private String createTime;
    private String updateTime;
    private String currency;
}
