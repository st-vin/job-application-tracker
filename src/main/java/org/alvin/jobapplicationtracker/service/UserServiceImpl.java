package org.alvin.jobapplicationtracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvin.jobapplicationtracker.dto.mapper.UserMapper;
import org.alvin.jobapplicationtracker.dto.request.UserRegistrationRequest;
import org.alvin.jobapplicationtracker.dto.response.UserResponseDTO;
import org.alvin.jobapplicationtracker.entity.UserEntity;
import org.alvin.jobapplicationtracker.exception.ResourceNotFoundException;
import org.alvin.jobapplicationtracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // Marks this as a Spring service component
@RequiredArgsConstructor // Lombok: generates constructor for final fields
@Slf4j // Lombok: generates logger
@Transactional // Makes all methods run in transactions
public class UserServiceImpl implements UserService {

    // Dependencies injected via constructor
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Business rule: Check if email already exists
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Convert DTO to entity
        UserEntity user = userMapper.toEntity(request);

        // Business logic: Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save to database
        UserEntity savedUser = userRepository.save(user);

        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Convert entity back to DTO
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true) // Optimization for read operations
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

        // Check if email changed and is already taken
        if (!user.getEmail().equalsIgnoreCase(request.getEmail()) &&
                userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Update fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        // Only update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
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
}