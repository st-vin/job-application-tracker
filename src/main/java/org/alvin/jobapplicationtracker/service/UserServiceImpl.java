package org.alvin.jobapplicationtracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvin.jobapplicationtracker.dto.mapper.UserMapper;
import org.alvin.jobapplicationtracker.dto.request.UserRegistrationRequest;
import org.alvin.jobapplicationtracker.dto.response.UserResponseDTO;
import org.alvin.jobapplicationtracker.entity.Role;
import org.alvin.jobapplicationtracker.entity.UserEntity;
import org.alvin.jobapplicationtracker.exception.ResourceNotFoundException;
import org.alvin.jobapplicationtracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int TOKEN_LENGTH = 32;
    private static final int TOKEN_EXPIRY_HOURS = 24;

    @Override
    public UserResponseDTO registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER); // Assign default role
        user.setEmailVerified(false);

        // Generate verification token
        String verificationToken = generateVerificationToken();
        user.setVerificationToken(verificationToken);
        user.setTokenExpiry(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));

        UserEntity savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Send verification email
        try {
            emailService.sendVerificationEmail(
                    savedUser.getEmail(),
                    verificationToken,
                    savedUser.getFirstName()
            );
            log.info("Verification email sent to: {}", savedUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", savedUser.getEmail(), e);
            // Don't fail registration if email fails - user can request resend
        }

        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.debug("Fetching user with ID: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);

        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.debug("Fetching all users");

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRegistrationRequest request) {
        log.info("Updating user with ID: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (!user.getEmail().equalsIgnoreCase(request.getEmail()) &&
                userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Optional: allow updating role if request contains it (ADMIN only)
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        UserEntity updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public void verifyEmail(String token) {
        UserEntity user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired verification token"));

        if (user.getTokenExpiry() != null && user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification token has expired");
        }

        if (user.getEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
        log.info("Email verified for user: {}", user.getEmail());
    }

    @Override
    public void resendVerificationEmail(String email) {
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (user.getEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }

        // Generate new token
        String verificationToken = generateVerificationToken();
        user.setVerificationToken(verificationToken);
        user.setTokenExpiry(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationToken, user.getFirstName());
        log.info("Verification email resent to: {}", user.getEmail());
    }

    private String generateVerificationToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
