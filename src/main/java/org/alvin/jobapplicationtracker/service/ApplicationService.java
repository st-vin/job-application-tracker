package org.alvin.jobapplicationtracker.service;

import org.alvin.jobapplicationtracker.dto.request.ApplicationCreateRequest;
import org.alvin.jobapplicationtracker.dto.response.ApplicationResponseDTO;
import org.alvin.jobapplicationtracker.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicationService {
    ApplicationResponseDTO createApplication(ApplicationCreateRequest request, Long userId);
    ApplicationResponseDTO getApplicationById(Long id);
    List<ApplicationResponseDTO> getApplicationsByUserId(Long userId);
    Page<ApplicationResponseDTO> getApplicationsByUserId(Long userId, Pageable pageable);
    List<ApplicationResponseDTO> getApplicationsByStatus(ApplicationStatus status);
    ApplicationResponseDTO updateApplication(Long id, ApplicationCreateRequest request);
    ApplicationResponseDTO updateApplicationStatus(Long id, ApplicationStatus newStatus);
    void deleteApplication(Long id);
    List<ApplicationResponseDTO> searchByCompany(String keyword);
    List<ApplicationResponseDTO> filterApplications(
            ApplicationStatus status,
            Double minSalary,
            Double maxSalary,
            String keyword);
}