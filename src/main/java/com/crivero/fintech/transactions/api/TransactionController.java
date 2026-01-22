package com.crivero.fintech.transactions.api;

import com.crivero.fintech.shared.domain.Money;
import com.crivero.fintech.transactions.api.dto.CreateTransferRequest;
import com.crivero.fintech.transactions.api.dto.TransactionResponse;
import com.crivero.fintech.transactions.application.TransferMoneyCommand;
import com.crivero.fintech.transactions.application.TransferMoneyResult;
import com.crivero.fintech.transactions.application.TransferMoneyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@SecurityRequirement(name = "ApiKeyAuth")
public class TransactionController {

  private final TransferMoneyService transferMoneyService;

  public TransactionController(TransferMoneyService transferMoneyService) {
    this.transferMoneyService = transferMoneyService;
  }

  @PostMapping
  public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody CreateTransferRequest request) {
    Objects.requireNonNull(request.amount(), "amount");
    TransferMoneyResult result =
        transferMoneyService.transfer(
            new TransferMoneyCommand(
                request.sourceAccountId(),
                request.destinationAccountId(),
                Money.of(request.amount())));

    TransactionResponse body =
        new TransactionResponse(
            result.transactionId(),
            result.sourceAccountId(),
            result.destinationAccountId(),
            result.amount(),
            result.createdAt());

    return ResponseEntity.created(URI.create("/transactions/" + result.transactionId())).body(body);
  }
}


