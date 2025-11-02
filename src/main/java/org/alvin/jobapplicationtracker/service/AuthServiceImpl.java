// service/AuthServiceImpl.java
package org.alvin.jobapplicationtracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvin.jobapplicationtracker.dto.mapper.UserMapper;
import org.alvin.jobapplicationtracker.dto.request.LoginRequest;
import org.alvin.jobapplicationtracker.dto.response.AuthResponse;
import org.alvin.jobapplicationtracker.dto.response.UserResponseDTO;
import org.alvin.jobapplicationtracker.entity.UserEntity;
import org.alvin.jobapplicationtracker.repository.UserRepository;
import org.alvin.jobapplicationtracker.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails);

        // Load user entity for response
        UserEntity user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponseDTO userDTO = userMapper.toResponseDTO(user);

        log.info("User '{}' logged in successfully", request.getEmail());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .user(userDTO)
                .build();
    }
}