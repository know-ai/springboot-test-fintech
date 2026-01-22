package com.crivero.fintech.accounts.api;

import com.crivero.fintech.accounts.api.dto.AccountResponse;
import com.crivero.fintech.accounts.api.dto.CreateAccountRequest;
import com.crivero.fintech.accounts.application.AccountService;
import com.crivero.fintech.accounts.domain.Account;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@SecurityRequirement(name = "ApiKeyAuth")
public class AccountController {

  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @PostMapping
  public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
    Long id = accountService.createAccount(request.initialBalance());
    Account account = accountService.getAccount(id);
    return ResponseEntity.created(URI.create("/accounts/" + id))
        .body(new AccountResponse(account.id(), account.balance().amount()));
  }

  @GetMapping("/{accountId}")
  public AccountResponse get(@PathVariable Long accountId) {
    Account account = accountService.getAccount(accountId);
    return new AccountResponse(account.id(), account.balance().amount());
  }
}


