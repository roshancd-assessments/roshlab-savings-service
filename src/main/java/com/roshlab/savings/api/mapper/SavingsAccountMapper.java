package com.roshlab.savings.api.mapper;

import com.roshlab.savings.api.dto.SavingsAccountRequest;
import com.roshlab.savings.api.dto.SavingsAccountResponse;
import com.roshlab.savings.entity.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SavingsAccountMapper {

    @Mapping(target = "accountNumber", ignore = true)
    SavingsAccount toDomain(SavingsAccountRequest request);

    SavingsAccountResponse toResponse(SavingsAccount account);
}
