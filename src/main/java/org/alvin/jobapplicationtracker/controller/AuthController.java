// controller/AuthController.java
package org.alvin.jobapplicationtracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvin.jobapplicationtracker.dto.request.LoginRequest;
import org.alvin.jobapplicationtracker.dto.response.AuthResponse;
import org.alvin.jobapplicationtracker.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("Login request received for: {}", request.getEmail());

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}