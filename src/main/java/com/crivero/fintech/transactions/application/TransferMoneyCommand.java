package com.crivero.fintech.transactions.application;

import com.crivero.fintech.shared.domain.Money;
import java.util.Objects;

public record TransferMoneyCommand(Long sourceAccountId, Long destinationAccountId, Money amount) {
  public TransferMoneyCommand {
    Objects.requireNonNull(sourceAccountId, "sourceAccountId");
    Objects.requireNonNull(destinationAccountId, "destinationAccountId");
    Objects.requireNonNull(amount, "amount");
  }
}


