package com.example.jwtusermanagement.security;

import com.example.jwtusermanagement.jwt.AuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AuthFilter authFilter;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void authenticatedUser_ShouldAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/api/protected"))
                .andExpect(status().isOk());
    }
    @Test
    void publicEndpoint_ShouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/public"))
                .andExpect(status().isOk());
    }
}
