package com.workflow.approval.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflow.approval.dto.request.LoginRequest;
import com.workflow.approval.dto.response.AuthResponse;
import com.workflow.approval.dto.response.UserResponse;
import com.workflow.approval.exception.GlobalExceptionHandler;
import com.workflow.approval.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock AuthService authService;
    @InjectMocks AuthController authController;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        UserResponse user = new UserResponse();
        user.setName("Karthik");
        user.setRole("EMPLOYEE");
        user.setEmail("karthik@gmail.com");

        AuthResponse response = new AuthResponse("jwt-token", user);
        when(authService.login(any())).thenReturn(response);

        LoginRequest req = new LoginRequest();
        req.setEmail("karthik@gmail.com");
        req.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.name").value("Karthik"))
                .andExpect(jsonPath("$.user.role").value("EMPLOYEE"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(authService.login(any())).thenThrow(new BadCredentialsException("Invalid Login"));

        LoginRequest req = new LoginRequest();
        req.setEmail("wrong@company.com");
        req.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid Login"));
    }

    @Test
    void login_missingEmail_returns400() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("");
        req.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_missingPassword_returns400() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("karthik@gmail.com");
        req.setPassword("");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_adminRole_returnsAdminInResponse() throws Exception {
        UserResponse user = new UserResponse();
        user.setName("System Admin"); user.setRole("ADMIN"); user.setEmail("admin@company.com");
        when(authService.login(any())).thenReturn(new AuthResponse("admin-token", user));

        LoginRequest req = new LoginRequest();
        req.setEmail("admin@company.com"); req.setPassword("Admin@123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.role").value("ADMIN"));
    }
}
