package com.crivero.fintech.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object para representar dinero.
 *
 * <p>Decisión: para este ejercicio usamos escala fija 2 (centavos) y BigDecimal.
 */
public final class Money {
  public static final int SCALE = 2;

  private final BigDecimal amount;

  private Money(BigDecimal amount) {
    this.amount = amount;
  }

  public static Money of(BigDecimal amount) {
    Objects.requireNonNull(amount, "amount");
    return new Money(amount.setScale(SCALE, RoundingMode.UNNECESSARY));
  }

  public static Money zero() {
    return new Money(BigDecimal.ZERO.setScale(SCALE, RoundingMode.UNNECESSARY));
  }

  public BigDecimal amount() {
    return amount;
  }

  public Money plus(Money other) {
    Objects.requireNonNull(other, "other");
    return new Money(this.amount.add(other.amount));
  }

  public Money minus(Money other) {
    Objects.requireNonNull(other, "other");
    return new Money(this.amount.subtract(other.amount));
  }

  public boolean isNegative() {
    return amount.signum() < 0;
  }

  public boolean isPositive() {
    return amount.signum() > 0;
  }

  public boolean isZero() {
    return amount.signum() == 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Money money)) return false;
    return amount.equals(money.amount);
  }

  @Override
  public int hashCode() {
    return amount.hashCode();
  }

  @Override
  public String toString() {
    return "Money{amount=" + amount + '}';
  }
}


