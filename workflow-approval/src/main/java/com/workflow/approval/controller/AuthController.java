package com.workflow.approval.controller;

import com.workflow.approval.dto.request.LoginRequest;
import com.workflow.approval.dto.response.AuthResponse;
import com.workflow.approval.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController 
{

    private final AuthService authService;

    public AuthController(AuthService authService) 
    {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) 
    {
        return ResponseEntity.ok(authService.login(request));
    }
}
