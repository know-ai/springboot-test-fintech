package com.crivero.fintech.api;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crivero.fintech.shared.security.ApiKeyAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TransactionsApiIT {

  @Autowired MockMvc mvc;

  @Test
  void transfer_updates_balances() throws Exception {
    String sourceLocation =
        mvc.perform(
                post("/accounts")
                    .header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key")
                    .contentType(APPLICATION_JSON)
                    .content("{\"initialBalance\": 10.00}"))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getHeader("Location");

    String destLocation =
        mvc.perform(
                post("/accounts")
                    .header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key")
                    .contentType(APPLICATION_JSON)
                    .content("{\"initialBalance\": 0.00}"))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getHeader("Location");

    long sourceId = Long.parseLong(sourceLocation.substring(sourceLocation.lastIndexOf('/') + 1));
    long destId = Long.parseLong(destLocation.substring(destLocation.lastIndexOf('/') + 1));

    mvc.perform(
            post("/transactions")
                .header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                        {"sourceAccountId": %d, "destinationAccountId": %d, "amount": 3.00}
                        """.formatted(sourceId, destId)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.amount", is(3.00)))
        .andExpect(jsonPath("$.sourceAccountId", is((int) sourceId)))
        .andExpect(jsonPath("$.destinationAccountId", is((int) destId)));

    mvc.perform(get(sourceLocation).header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance", is(7.00)));

    mvc.perform(get(destLocation).header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance", is(3.00)));
  }

  @Test
  void transfer_rejects_insufficient_funds_and_keeps_balances() throws Exception {
    String sourceLocation =
        mvc.perform(
                post("/accounts")
                    .header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key")
                    .contentType(APPLICATION_JSON)
                    .content("{\"initialBalance\": 1.00}"))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getHeader("Location");

    String destLocation =
        mvc.perform(
                post("/accounts")
                    .header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key")
                    .contentType(APPLICATION_JSON)
                    .content("{\"initialBalance\": 0.00}"))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getHeader("Location");

    long sourceId = Long.parseLong(sourceLocation.substring(sourceLocation.lastIndexOf('/') + 1));
    long destId = Long.parseLong(destLocation.substring(destLocation.lastIndexOf('/') + 1));

    mvc.perform(
            post("/transactions")
                .header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                        {"sourceAccountId": %d, "destinationAccountId": %d, "amount": 2.00}
                        """.formatted(sourceId, destId)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error", is("INSUFFICIENT_FUNDS")));

    mvc.perform(get(sourceLocation).header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance", is(1.00)));

    mvc.perform(get(destLocation).header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance", is(0.00)));
  }
}


