package com.tp.order.service;

import com.tp.order.dto.AuthResponse;
import com.tp.order.dto.LoginRequest;
import com.tp.order.dto.RegisterRequest;
import com.tp.order.entity.User;
import com.tp.order.entity.UserRole;
import com.tp.order.exception.UserAlreadyExistsException;
import com.tp.order.repository.UserRepository;
import com.tp.order.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("john")
                .email("john@test.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();
    }

    // ================= REGISTER TESTS =================

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest(
                "abhi",
                "abhi@test.com",
                "password",
                UserRole.USER
        );

        when(userRepository.existsByUsername("abhi")).thenReturn(false);
        when(userRepository.existsByEmail("abhi@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtUtil.generateToken("abhi")).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("abhi", response.username());
        assertEquals("abhi@test.com", response.email());
        assertEquals("USER", response.role());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_usernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "abhi",
                "abhi@test.com",
                "password",
                UserRole.USER
        );

        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.register(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_emailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "abhi",
                "abhi@test.com",
                "password",
                UserRole.USER
        );

        when(userRepository.existsByUsername("abhi")).thenReturn(false);
        when(userRepository.existsByEmail("abhi@test.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.register(request));
    }

    @Test
    void register_nullRole_defaultsToUser() {
        RegisterRequest request = new RegisterRequest(
                "abhi",
                "abhi@test.com",
                "password",
                null
        );

        when(userRepository.existsByUsername("abhi")).thenReturn(false);
        when(userRepository.existsByEmail("abhi@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtUtil.generateToken("abhi")).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertEquals("USER", response.role());
    }

    // ================= LOGIN TESTS =================

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("abhi", "password");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByUsername("abhi"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken("abhi")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("abhi", response.username());
        assertEquals("abhi@test.com", response.email());
        assertEquals("USER", response.role());
    }

    @Test
    void login_userNotFound() {
        LoginRequest request = new LoginRequest("abhi", "password");

        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));

        when(userRepository.findByUsername("abhi"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authService.login(request));
    }
}
