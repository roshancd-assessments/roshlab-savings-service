package com.roshlab.savings.api.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SavingsAccountResponse {
    String id;
    String accountNumber;
    String customerName;
    String accountNickName;
}