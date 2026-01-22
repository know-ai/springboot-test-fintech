package com.crivero.fintech.accounts.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CreateAccountRequest(
    @NotNull @PositiveOrZero BigDecimal initialBalance
) {}


