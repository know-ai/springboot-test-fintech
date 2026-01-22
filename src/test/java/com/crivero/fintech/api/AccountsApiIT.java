package com.crivero.fintech.api;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
class AccountsApiIT {

  @Autowired MockMvc mvc;

  @Test
  void create_and_get_account() throws Exception {
    String body = """
        {"initialBalance": 100.00}
        """;

    String location =
        mvc.perform(
                post("/accounts")
                    .header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key")
                    .contentType(APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("/accounts/")))
            .andExpect(jsonPath("$.balance", is(100.00)))
            .andReturn()
            .getResponse()
            .getHeader("Location");

    mvc.perform(get(location).header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance", is(100.00)));
  }

  @Test
  void create_account_rejects_negative_balance() throws Exception {
    mvc.perform(
            post("/accounts")
                .header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key")
                .contentType(APPLICATION_JSON)
                .content("{\"initialBalance\": -1.00}"))
        .andExpect(status().isBadRequest());
  }
}


