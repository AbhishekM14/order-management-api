package com.tp.order.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tp.order.dto.AuthResponse;
import com.tp.order.dto.LoginRequest;
import com.tp.order.dto.RegisterRequest;
import com.tp.order.entity.User;
import com.tp.order.entity.UserRole;
import com.tp.order.exception.UserAlreadyExistsException;
import com.tp.order.repository.UserRepository;
import com.tp.order.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering a new user>>>>>>>>>");
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Username already exists >>>>>>>>>>");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already exists >>>>>>>>>");
        }
        
        // Handle null role - use default USER role
        UserRole role = request.role() != null ? request.role() : UserRole.USER;
        
        // Create new user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();
        
        userRepository.save(user);
        log.info("User registration completed successfully>>>>>>");
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());
        
        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        log.info("User login requested>>>>>>>");
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        
        // Fetch user details
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());
        
        log.info("User logged in completed successfully>>>>>>>>");
        
        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
