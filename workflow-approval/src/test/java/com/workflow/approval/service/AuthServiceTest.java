package com.workflow.approval.service;

import com.workflow.approval.dto.request.LoginRequest;
import com.workflow.approval.dto.response.AuthResponse;
import com.workflow.approval.entity.User;
import com.workflow.approval.exception.BadRequestException;
import com.workflow.approval.repository.UserRepository;
import com.workflow.approval.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock UserDetailsService userDetailsService;
    @Mock UserRepository userRepository;
    @Mock JwtUtil jwtUtil;

    @InjectMocks AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmployeeId("EMP001");
        testUser.setName("Karthik");
        testUser.setEmail("karthik@gmail.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.EMPLOYEE);
        testUser.setDepartment("IT");
        testUser.setActive(true);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("karthik@gmail.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void login_withValidCredentials_returnsAuthResponse() {
        UserDetails mockDetails = org.springframework.security.core.userdetails.User
                .withUsername("karthik@gmail.com").password("encoded").roles("EMPLOYEE").build();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("karthik@gmail.com")).thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername("karthik@gmail.com")).thenReturn(mockDetails);
        when(jwtUtil.generateToken(any(), eq("EMPLOYEE"))).thenReturn("jwt-token-abc");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token-abc", response.getToken());
        assertEquals("Karthik", response.getUser().getName());
        assertEquals("EMPLOYEE", response.getUser().getRole());
    }

    @Test
    void login_withEmptyEmail_throwsBadRequestException() {
        loginRequest.setEmail("");
        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_withEmptyPassword_throwsBadRequestException() {
        loginRequest.setPassword("");
        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_withWrongCredentials_throwsException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid Login"));
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_userWithManager_setsManagerInfo() {
        User manager = new User();
        manager.setId(2L);
        manager.setName("Veerakumar");
        testUser.setManager(manager);

        UserDetails mockDetails = org.springframework.security.core.userdetails.User
                .withUsername("karthik@gmail.com").password("encoded").roles("EMPLOYEE").build();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername(any())).thenReturn(mockDetails);
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");

        AuthResponse response = authService.login(loginRequest);

        assertEquals(2L, response.getUser().getManagerId());
        assertEquals("Veerakumar", response.getUser().getManagerName());
    }
}
