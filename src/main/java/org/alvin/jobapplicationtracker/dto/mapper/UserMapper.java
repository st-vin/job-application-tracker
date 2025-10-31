package org.alvin.jobapplicationtracker.dto.mapper;

import org.alvin.jobapplicationtracker.dto.request.UserRegistrationRequest;
import org.alvin.jobapplicationtracker.dto.response.UserResponseDTO;
import org.alvin.jobapplicationtracker.dto.response.UserSummaryDTO;
import org.alvin.jobapplicationtracker.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component // Makes this a Spring bean
public class UserMapper {

    // Entity -> Response DTO
    public UserResponseDTO toResponseDTO(UserEntity user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    // Entity -> Summary DTO (for nested objects)
    public UserSummaryDTO toSummaryDTO(UserEntity user) {
        if (user == null) return null;

        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
    }

    // Request DTO -> Entity (for creation)
    public UserEntity toEntity(UserRegistrationRequest request) {
        if (request == null) return null;

        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        // Password will be encoded in the service layer
        user.setPassword(request.getPassword());
        return user;
    }
}