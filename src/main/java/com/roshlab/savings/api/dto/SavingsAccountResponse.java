package com.roshlab.savings.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SavingsAccountResponse {
    String accountNumber;
    String customerName;
    String accountNickName;
}