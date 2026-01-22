package com.crivero.fintech.transactions.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
    Long id,
    Long sourceAccountId,
    Long destinationAccountId,
    BigDecimal amount,
    Instant createdAt
) {}


