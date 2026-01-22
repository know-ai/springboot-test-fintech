package com.crivero.fintech.accounts.domain;

import com.crivero.fintech.shared.domain.Money;
import com.crivero.fintech.shared.domain.exception.DomainException;
import java.util.Objects;

/**
 * Aggregate Root (modelo de dominio) para una cuenta.
 *
 * <p>Nota: para este ejercicio modelamos solo saldo e invariantes de débito/crédito.</p>
 */
public final class Account {
  private final Long id;
  private Money balance;

  private Account(Long id, Money balance) {
    this.id = id;
    this.balance = balance;
  }

  public static Account of(Long id, Money balance) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(balance, "balance");
    if (balance.isNegative()) throw new InvalidAccountState("balance cannot be negative");
    return new Account(id, balance);
  }

  public Long id() {
    return id;
  }

  public Money balance() {
    return balance;
  }

  public void credit(Money amount) {
    requirePositive(amount, "credit amount must be positive");
    this.balance = this.balance.plus(amount);
  }

  public void debit(Money amount) {
    requirePositive(amount, "debit amount must be positive");
    Money newBalance = this.balance.minus(amount);
    if (newBalance.isNegative()) throw new InsufficientFunds("insufficient funds");
    this.balance = newBalance;
  }

  private static void requirePositive(Money amount, String message) {
    Objects.requireNonNull(amount, "amount");
    if (!amount.isPositive()) throw new InvalidAccountOperation(message);
  }

  public static final class InvalidAccountState extends DomainException {
    public InvalidAccountState(String message) {
      super(message);
    }
  }

  public static final class InvalidAccountOperation extends DomainException {
    public InvalidAccountOperation(String message) {
      super(message);
    }
  }

  public static final class InsufficientFunds extends DomainException {
    public InsufficientFunds(String message) {
      super(message);
    }
  }
}


