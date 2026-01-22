package com.crivero.fintech.shared.api;

import java.time.Instant;

public record ErrorResponse(
    String error,
    String message,
    Instant timestamp
) {}


