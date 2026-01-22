package com.crivero.fintech.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.crivero.fintech.accounts.application.AccountService;
import com.crivero.fintech.accounts.infrastructure.persistence.AccountJpaRepository;
import com.crivero.fintech.transactions.application.TransferMoneyCommand;
import com.crivero.fintech.transactions.application.TransferMoneyService;
import com.crivero.fintech.transactions.infrastructure.persistence.TransactionJpaRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("postgres-it")
class PostgresSmokeIT extends PostgresContainerBase {

  @Autowired AccountService accountService;
  @Autowired TransferMoneyService transferMoneyService;
  @Autowired AccountJpaRepository accountRepository;
  @Autowired TransactionJpaRepository transactionRepository;

  @Test
  void transfer_persists_in_postgres_and_is_queryable() {
    Long a = accountService.createAccount(new BigDecimal("10.00"));
    Long b = accountService.createAccount(new BigDecimal("0.00"));

    transferMoneyService.transfer(
        new TransferMoneyCommand(a, b, com.crivero.fintech.shared.domain.Money.of(new BigDecimal("3.00"))));

    assertEquals(new BigDecimal("7.00"), accountRepository.findById(a).orElseThrow().getBalance());
    assertEquals(new BigDecimal("3.00"), accountRepository.findById(b).orElseThrow().getBalance());
    assertEquals(1, transactionRepository.count());
  }
}


