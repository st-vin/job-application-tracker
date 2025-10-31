package org.alvin.jobapplicationtracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvin.jobapplicationtracker.dto.request.UserRegistrationRequest;
import org.alvin.jobapplicationtracker.dto.response.UserResponseDTO;
import org.alvin.jobapplicationtracker.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Combines @Controller + @ResponseBody
@RequestMapping("/api/users") // Base path for all endpoints
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    /**
     * Register a new user
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());

        UserResponseDTO user = userService.registerUser(request);

        // Return 201 Created with the new user
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.debug("Request to get user with ID: {}", id);

        UserResponseDTO user = userService.getUserById(id);

        return ResponseEntity.ok(user); // 200 OK
    }

    /**
     * Get user by email
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        log.debug("Request to get user with email: {}", email);

        UserResponseDTO user = userService.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.debug("Request to get all users");

        List<UserResponseDTO> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    /**
     * Update user
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegistrationRequest request) {
        log.info("Update request for user ID: {}", id);

        UserResponseDTO user = userService.updateUser(id, request);

        return ResponseEntity.ok(user);
    }

    /**
     * Delete user
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Delete request for user ID: {}", id);

        userService.deleteUser(id);

        // Return 204 No Content (success with no response body)
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if email exists
     * GET /api/users/exists?email=test@example.com
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        log.debug("Checking if email exists: {}", email);

        boolean exists = userService.existsByEmail(email);

        return ResponseEntity.ok(exists);
    }
}