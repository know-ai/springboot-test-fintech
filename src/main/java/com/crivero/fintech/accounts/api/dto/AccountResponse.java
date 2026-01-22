package com.crivero.fintech.accounts.api.dto;

import java.math.BigDecimal;

public record AccountResponse(
    Long id,
    BigDecimal balance
) {}


