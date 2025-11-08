package org.alvin.jobapplicationtracker.service;

import org.alvin.jobapplicationtracker.dto.request.UserRegistrationRequest;
import org.alvin.jobapplicationtracker.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO registerUser(UserRegistrationRequest request);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByEmail(String email);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(Long id, UserRegistrationRequest request);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
    void verifyEmail(String token);
    void resendVerificationEmail(String email);
}