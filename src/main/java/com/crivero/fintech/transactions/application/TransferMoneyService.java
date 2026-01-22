package com.crivero.fintech.transactions.application;

import com.crivero.fintech.accounts.domain.Account;
import com.crivero.fintech.accounts.infrastructure.persistence.AccountEntity;
import com.crivero.fintech.accounts.infrastructure.persistence.AccountJpaRepository;
import com.crivero.fintech.shared.domain.Money;
import com.crivero.fintech.shared.domain.exception.DomainException;
import com.crivero.fintech.transactions.infrastructure.persistence.TransactionEntity;
import com.crivero.fintech.transactions.infrastructure.persistence.TransactionJpaRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferMoneyService {
  private final AccountJpaRepository accountRepository;
  private final TransactionJpaRepository transactionRepository;

  public TransferMoneyService(
      AccountJpaRepository accountRepository,
      TransactionJpaRepository transactionRepository) {
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
  }

  /**
   * Estrategia de concurrencia: optimistic locking (@Version) + reintento acotado.
   *
   * <p>Si dos transferencias compiten por actualizar el mismo saldo, una fallará con
   * {@link ObjectOptimisticLockingFailureException} y se reintenta el caso de uso.</p>
   */
  @Retryable(
      retryFor = ObjectOptimisticLockingFailureException.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 30, multiplier = 2))
  @Transactional
  public TransferMoneyResult transfer(TransferMoneyCommand command) {
    if (command.sourceAccountId().equals(command.destinationAccountId())) {
      throw new InvalidTransfer("source and destination accounts must be different");
    }
    if (!command.amount().isPositive()) {
      throw new InvalidTransfer("transfer amount must be positive");
    }

    AccountEntity source =
        accountRepository.findById(command.sourceAccountId())
            .orElseThrow(() -> new AccountNotFound(command.sourceAccountId()));
    AccountEntity destination =
        accountRepository.findById(command.destinationAccountId())
            .orElseThrow(() -> new AccountNotFound(command.destinationAccountId()));

    Money amount = command.amount();
    Account sourceDomain = Account.of(source.getId(), Money.of(source.getBalance()));
    Account destDomain = Account.of(destination.getId(), Money.of(destination.getBalance()));

    sourceDomain.debit(amount);
    destDomain.credit(amount);

    source.setBalance(sourceDomain.balance().amount());
    destination.setBalance(destDomain.balance().amount());

    // Guardar ambas cuentas (incrementa @Version). Si hay conflicto -> OptimisticLockingFailure -> retry.
    accountRepository.save(source);
    accountRepository.save(destination);
    accountRepository.flush();

    TransactionEntity tx =
        transactionRepository.save(
            new TransactionEntity(
                source.getId(),
                destination.getId(),
                amount.amount()));

    return new TransferMoneyResult(
        tx.getId(),
        tx.getSourceAccountId(),
        tx.getDestinationAccountId(),
        tx.getAmount(),
        tx.getCreatedAt());
  }

  public static final class AccountNotFound extends DomainException {
    public AccountNotFound(Long accountId) {
      super("account not found: " + accountId);
    }
  }

  public static final class InvalidTransfer extends DomainException {
    public InvalidTransfer(String message) {
      super(message);
    }
  }
}


