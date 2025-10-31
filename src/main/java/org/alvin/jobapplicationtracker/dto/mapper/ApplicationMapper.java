// dto/mapper/ApplicationMapper.java
package org.alvin.jobapplicationtracker.dto.mapper;

import org.alvin.jobapplicationtracker.dto.request.ApplicationCreateRequest;
import org.alvin.jobapplicationtracker.dto.response.ApplicationResponseDTO;
import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

    private final UserMapper userMapper;

    // Constructor injection of UserMapper
    public ApplicationMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // Entity -> Response DTO
    public ApplicationResponseDTO toResponseDTO(ApplicationEntity application) {
        if (application == null) return null;

        ApplicationResponseDTO dto = new ApplicationResponseDTO();
        dto.setId(application.getId());
        dto.setCompanyName(application.getCompanyName());
        dto.setPosition(application.getPosition());
        dto.setStatus(application.getStatus());
        dto.setAppliedDate(application.getAppliedDate());
        dto.setSalary(application.getSalary());
        dto.setJobBoardSource(application.getJobBoardSource());
        dto.setJobUrl(application.getJobUrl());
        dto.setNotes(application.getNotes());
        dto.setCreatedAt(application.getCreatedAt());
        dto.setUpdatedAt(application.getUpdatedAt());

        // Nested user info
        dto.setUser(userMapper.toSummaryDTO(application.getUser()));

        // Counts instead of full lists (avoids loading all data)
        dto.setReminderCount(
                application.getReminders() != null ? application.getReminders().size() : 0
        );
        dto.setInterviewNoteCount(
                application.getInterviewNotes() != null ? application.getInterviewNotes().size() : 0
        );
        dto.setStatusHistoryCount(
                application.getStatusHistory() != null ? application.getStatusHistory().size() : 0
        );

        return dto;
    }

    // Request DTO -> Entity
    public ApplicationEntity toEntity(ApplicationCreateRequest request) {
        if (request == null) return null;

        ApplicationEntity application = new ApplicationEntity();
        application.setCompanyName(request.getCompanyName());
        application.setPosition(request.getPosition());
        application.setStatus(request.getStatus());
        application.setAppliedDate(request.getAppliedDate());
        application.setSalary(request.getSalary());
        application.setJobBoardSource(request.getJobBoardSource());
        application.setJobUrl(request.getJobUrl());
        application.setNotes(request.getNotes());
        // User will be set in the service layer
        return application;
    }
}