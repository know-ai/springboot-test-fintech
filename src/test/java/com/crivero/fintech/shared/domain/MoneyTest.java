package com.crivero.fintech.shared.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

  @Test
  void of_normalizes_to_scale_2_when_possible() {
    Money m = Money.of(new BigDecimal("10.5"));
    assertEquals(new BigDecimal("10.50"), m.amount());
  }

  @Test
  void of_rejects_more_than_2_decimals() {
    assertThrows(ArithmeticException.class, () -> Money.of(new BigDecimal("1.001")));
  }
}


