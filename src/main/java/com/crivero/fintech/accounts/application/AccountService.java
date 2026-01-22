package com.crivero.fintech.accounts.application;

import com.crivero.fintech.accounts.domain.Account;
import com.crivero.fintech.accounts.infrastructure.persistence.AccountEntity;
import com.crivero.fintech.accounts.infrastructure.persistence.AccountJpaRepository;
import com.crivero.fintech.shared.domain.Money;
import com.crivero.fintech.shared.domain.exception.DomainException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
  private final AccountJpaRepository accountRepository;

  public AccountService(AccountJpaRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Transactional
  public Long createAccount(BigDecimal initialBalance) {
    Money initial = Money.of(initialBalance);
    if (initial.isNegative()) throw new InvalidAccountRequest("initialBalance must be >= 0");

    AccountEntity saved = accountRepository.save(new AccountEntity(initial.amount()));
    return saved.getId();
  }

  @Transactional(readOnly = true)
  public Account getAccount(Long id) {
    AccountEntity entity =
        accountRepository.findById(id).orElseThrow(() -> new AccountNotFound(id));
    return Account.of(entity.getId(), Money.of(entity.getBalance()));
  }

  public static final class AccountNotFound extends DomainException {
    public AccountNotFound(Long id) {
      super("account not found: " + id);
    }
  }

  public static final class InvalidAccountRequest extends DomainException {
    public InvalidAccountRequest(String message) {
      super(message);
    }
  }
}


