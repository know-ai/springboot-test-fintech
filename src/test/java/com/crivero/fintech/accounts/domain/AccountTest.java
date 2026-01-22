package com.crivero.fintech.accounts.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.crivero.fintech.shared.domain.Money;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AccountTest {

  @Test
  void debit_and_credit_update_balance() {
    Account a = Account.of(1L, Money.of(new BigDecimal("100.00")));
    a.debit(Money.of(new BigDecimal("40.00")));
    a.credit(Money.of(new BigDecimal("10.00")));
    assertEquals(new BigDecimal("70.00"), a.balance().amount());
  }

  @Test
  void debit_rejects_insufficient_funds() {
    Account a = Account.of(1L, Money.of(new BigDecimal("10.00")));
    assertThrows(Account.InsufficientFunds.class, () -> a.debit(Money.of(new BigDecimal("11.00"))));
  }
}


