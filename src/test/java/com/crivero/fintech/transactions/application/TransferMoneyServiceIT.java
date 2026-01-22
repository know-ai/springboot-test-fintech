package com.crivero.fintech.transactions.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.crivero.fintech.accounts.application.AccountService;
import com.crivero.fintech.accounts.infrastructure.persistence.AccountEntity;
import com.crivero.fintech.accounts.infrastructure.persistence.AccountJpaRepository;
import com.crivero.fintech.shared.domain.Money;
import com.crivero.fintech.transactions.infrastructure.persistence.TransactionJpaRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TransferMoneyServiceIT {

  @Autowired TransferMoneyService transferMoneyService;
  @Autowired AccountService accountService;
  @Autowired AccountJpaRepository accountRepository;
  @Autowired TransactionJpaRepository transactionRepository;

  @Test
  void transfer_is_atomic_when_insufficient_funds() {
    Long sourceId = accountService.createAccount(new BigDecimal("10.00"));
    Long destId = accountService.createAccount(new BigDecimal("0.00"));

    assertThrows(
        com.crivero.fintech.accounts.domain.Account.InsufficientFunds.class,
        () -> transferMoneyService.transfer(
            new TransferMoneyCommand(sourceId, destId, Money.of(new BigDecimal("11.00")))));

    AccountEntity source = accountRepository.findById(sourceId).orElseThrow();
    AccountEntity dest = accountRepository.findById(destId).orElseThrow();
    assertEquals(new BigDecimal("10.00"), source.getBalance());
    assertEquals(new BigDecimal("0.00"), dest.getBalance());
    assertEquals(0, transactionRepository.count());
  }

  @Test
  void concurrent_transfers_keep_consistent_balances() throws Exception {
    Long sourceId = accountService.createAccount(new BigDecimal("1000.00"));
    Long destId = accountService.createAccount(new BigDecimal("0.00"));

    int n = 100;
    ExecutorService pool = Executors.newFixedThreadPool(10);
    CountDownLatch ready = new CountDownLatch(n);
    CountDownLatch start = new CountDownLatch(1);
    List<Future<?>> futures = new ArrayList<>();

    for (int i = 0; i < n; i++) {
      futures.add(
          pool.submit(
              () -> {
                ready.countDown();
                start.await();
                transferMoneyService.transfer(
                    new TransferMoneyCommand(
                        sourceId, destId, Money.of(new BigDecimal("1.00"))));
                return null;
              }));
    }

    ready.await();
    start.countDown();
    for (Future<?> f : futures) {
      f.get();
    }
    pool.shutdown();

    AccountEntity source = accountRepository.findById(sourceId).orElseThrow();
    AccountEntity dest = accountRepository.findById(destId).orElseThrow();
    assertEquals(new BigDecimal("900.00"), source.getBalance());
    assertEquals(new BigDecimal("100.00"), dest.getBalance());
    assertEquals(n, transactionRepository.count());
  }
}


