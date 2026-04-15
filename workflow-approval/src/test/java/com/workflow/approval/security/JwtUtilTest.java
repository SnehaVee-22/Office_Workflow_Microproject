package com.workflow.approval.security;

import com.workflow.approval.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RzLW9ubHktbm90LXByb2Q=");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);

        userDetails = User.withUsername("karthik@gmail.com")
                .password("encoded")
                .roles("EMPLOYEE")
                .build();
    }

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtUtil.generateToken(userDetails, "EMPLOYEE");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractEmail_fromValidToken_returnsCorrectEmail() {
        String token = jwtUtil.generateToken(userDetails, "EMPLOYEE");
        String email = jwtUtil.extractEmail(token);
        assertEquals("karthik@gmail.com", email);
    }

    @Test
    void extractRole_fromValidToken_returnsCorrectRole() {
        String token = jwtUtil.generateToken(userDetails, "MANAGER");
        String role = jwtUtil.extractRole(token);
        assertEquals("MANAGER", role);
    }

    @Test
    void validateToken_withValidToken_returnsTrue() {
        String token = jwtUtil.generateToken(userDetails, "EMPLOYEE");
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }


    @Test
    void isTokenExpired_freshToken_returnsFalse() {
        String token = jwtUtil.generateToken(userDetails, "EMPLOYEE");
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    

    @Test
    void generateToken_differentRoles_produceDifferentTokens() {
        String tokenEmp = jwtUtil.generateToken(userDetails, "EMPLOYEE");
        String tokenMgr = jwtUtil.generateToken(userDetails, "MANAGER");
        assertNotEquals(tokenEmp, tokenMgr);
    }
}
