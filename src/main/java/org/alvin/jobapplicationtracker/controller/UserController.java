package org.alvin.jobapplicationtracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvin.jobapplicationtracker.dto.request.UserRegistrationRequest;
import org.alvin.jobapplicationtracker.dto.response.UserResponseDTO;
import org.alvin.jobapplicationtracker.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    /**
     * Register a new user (public)
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());

        UserResponseDTO user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Get user by ID (ADMIN only)
     * GET /api/users/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.debug("ADMIN fetching user with ID: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Get user by email (ADMIN only)
     * GET /api/users/email/{email}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        log.debug("ADMIN fetching user with email: {}", email);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Get all users (ADMIN only)
     * GET /api/users
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.debug("ADMIN fetching all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Update user
     * PUT /api/users/{id}
     * - Admins can update anyone
     * - Users can only update themselves
     */
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegistrationRequest request) {
        log.info("User update request for ID: {}", id);
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Delete user
     * DELETE /api/users/{id}
     * - Only ADMINs can delete
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("ADMIN deleting user ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if email exists (public)
     * GET /api/users/exists?email=test@example.com
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        log.debug("Checking if email exists: {}", email);
        return ResponseEntity.ok(userService.existsByEmail(email));
    }
}
