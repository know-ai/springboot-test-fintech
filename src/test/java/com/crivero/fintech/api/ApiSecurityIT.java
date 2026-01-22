package com.crivero.fintech.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class ApiSecurityIT {

  @Autowired MockMvc mvc;

  @Test
  void missing_api_key_returns_401() throws Exception {
    mvc.perform(get("/accounts/1"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void valid_api_key_allows_access() throws Exception {
    mvc.perform(get("/accounts/1").header(ApiKeyAuthFilter.API_KEY_HEADER, "test-key"))
        .andExpect(status().isNotFound()); // cuenta no existe, pero auth pasó
  }
}


