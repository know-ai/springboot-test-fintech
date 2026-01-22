package com.crivero.fintech.transactions.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateTransferRequest(
    @NotNull Long sourceAccountId,
    @NotNull Long destinationAccountId,
    @NotNull @Positive BigDecimal amount
) {}


