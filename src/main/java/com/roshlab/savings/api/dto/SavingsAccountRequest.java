package com.roshlab.savings.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SavingsAccountRequest {
    @NotBlank(message = "Customer name is required")
    String customerName;

    @NotBlank(message = "Account nickname is required")
    @Size(min = 5, max = 30, message = "Account nickname must be between 5 and 30 characters")
    String accountNickName;
}