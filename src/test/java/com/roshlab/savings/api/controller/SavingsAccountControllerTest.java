package com.roshlab.savings.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roshlab.savings.api.dto.SavingsAccountRequest;
import com.roshlab.savings.api.dto.SavingsAccountResponse;
import com.roshlab.savings.api.mapper.SavingsAccountMapper;
import com.roshlab.savings.entity.SavingsAccount;
import com.roshlab.savings.service.SavingsAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SavingsAccountController.class)
class SavingsAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SavingsAccountService savingsAccountService;

    @MockitoBean
    private SavingsAccountMapper savingsAccountMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateSavingsAccount_whenValidRequestIsGiven() throws Exception {
        // Arrange : Savings account creation request
        SavingsAccountRequest request = SavingsAccountRequest.builder()
                .customerName("Roshan Deniyage")
                .accountNickName("EmergencyFund")
                .build();

        // Arrange : Converted domain object
        SavingsAccount savingsAccount = SavingsAccount.builderWithId()
                .id(1L)
                .customerName(request.getCustomerName())
                .accountNickName(request.getAccountNickName())
                .accountNumber("123456789123")
                .createdAt(OffsetDateTime.now())
                .build();

        // Arrange: Response object
        SavingsAccountResponse response = SavingsAccountResponse.builder()
                .customerName(savingsAccount.getCustomerName())
                .accountNumber(savingsAccount.getAccountNumber())
                .accountNickName(savingsAccount.getAccountNickName())
                .build();

        // Mocking the service and mapper methods
        when(savingsAccountMapper.toDomain(any(SavingsAccountRequest.class))).thenReturn(savingsAccount);
        when(savingsAccountService.createAccount(any(SavingsAccount.class))).thenReturn(savingsAccount);
        when(savingsAccountMapper.toResponse(any(SavingsAccount.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("Roshan Deniyage"));
    }
}