package com.roshlab.savings.api.controller;

import com.roshlab.savings.api.dto.SavingsAccountRequest;
import com.roshlab.savings.api.dto.SavingsAccountResponse;
import com.roshlab.savings.api.mapper.SavingsAccountMapper;
import com.roshlab.savings.entity.SavingsAccount;
import com.roshlab.savings.service.SavingsAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;
    private final SavingsAccountMapper savingsAccountMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountResponse createAccount(@Valid @RequestBody SavingsAccountRequest savingsAccountRequest) {
        SavingsAccount savingsAccount = savingsAccountMapper.toDomain(savingsAccountRequest);
        SavingsAccount savingsAccountCreated = savingsAccountService.createAccount(savingsAccount);
        return savingsAccountMapper.toResponse(savingsAccountCreated);
    }

    @GetMapping("/{id}")
    public SavingsAccountResponse getAccount(@PathVariable Long id) {
        CompletableFuture<SavingsAccount> future = savingsAccountService.getAccount(id);
        SavingsAccount savingsAccount = future.join();
        return savingsAccountMapper.toResponse(savingsAccount);
    }

    @GetMapping
    public List<SavingsAccountResponse> getAccountsByCustomerName(@RequestParam String customerName) {
        List<SavingsAccount> accounts = savingsAccountService.getAccountsByCustomerName(customerName);
        return accounts.stream()
                .map(savingsAccountMapper::toResponse)
                .collect(Collectors.toList());
    }
}