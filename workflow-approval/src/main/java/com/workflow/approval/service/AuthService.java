package com.workflow.approval.service;

import com.workflow.approval.dto.request.LoginRequest;
import com.workflow.approval.dto.response.AuthResponse;
import com.workflow.approval.dto.response.UserResponse;
import com.workflow.approval.entity.User;
import com.workflow.approval.exception.BadRequestException;
import com.workflow.approval.repository.UserRepository;
import com.workflow.approval.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       UserRepository userRepository,
                       JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Required Fields Missing");
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BadRequestException("Invalid Login"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        UserResponse userResponse = mapToUserResponse(user);
        return new AuthResponse(token, userResponse);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setEmployeeId(user.getEmployeeId());
        r.setName(user.getName());
        r.setEmail(user.getEmail());
        r.setRole(user.getRole().name());
        r.setDepartment(user.getDepartment());
        r.setActive(user.isActive());
        if (user.getManager() != null) {
            r.setManagerId(user.getManager().getId());
            r.setManagerName(user.getManager().getName());
        }
        return r;
    }
}
