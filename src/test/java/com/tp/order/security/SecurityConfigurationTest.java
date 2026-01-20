package com.tp.order.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;



@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    void contextLoads_andSecurityBeansAreCreated() {
        assert applicationContext.getBean(SecurityConfiguration.class) != null;
        assert passwordEncoder != null;
        assert authenticationManager != null;
    }

    @Test
    void publicEndpoints_shouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_shouldReturnUnauthorized_whenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void productsEndpoint_shouldBeAccessible_forUserRole() throws Exception {
        mockMvc.perform(get("/api/products")
                        .with(jwt().authorities(() -> "USER")))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_shouldBeForbidden_forNonAdminRole() throws Exception {
    	mockMvc.perform(get("/api/products")
    	        .with(jwt(jwt -> jwt.claim("roles", List.of("PREMIUM_USER")))))
    	        .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_shouldBeAccessible_forAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin")
                        .with(jwt().authorities(() -> "ADMIN")))
                .andExpect(status().isOk());
    }
}