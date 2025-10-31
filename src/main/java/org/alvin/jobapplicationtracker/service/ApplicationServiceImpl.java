package org.alvin.jobapplicationtracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvin.jobapplicationtracker.dto.mapper.ApplicationMapper;
import org.alvin.jobapplicationtracker.dto.request.ApplicationCreateRequest;
import org.alvin.jobapplicationtracker.dto.response.ApplicationResponseDTO;
import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.alvin.jobapplicationtracker.entity.ApplicationStatus;
import org.alvin.jobapplicationtracker.entity.StatusHistory;
import org.alvin.jobapplicationtracker.entity.UserEntity;
import org.alvin.jobapplicationtracker.exception.ResourceNotFoundException;
import org.alvin.jobapplicationtracker.repository.ApplicationRepository;
import org.alvin.jobapplicationtracker.repository.StatusHistoryRepository;
import org.alvin.jobapplicationtracker.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final ApplicationMapper applicationMapper;

    @Override
    public ApplicationResponseDTO createApplication(
            ApplicationCreateRequest request, Long userId) {
        log.info("Creating application for user ID: {}", userId);

        // Fetch user
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Business rule: Check for duplicate applications
        if (applicationRepository.existsByUserIdAndCompanyName(
                userId, request.getCompanyName())) {
            throw new IllegalArgumentException(
                    "You already have an application at " + request.getCompanyName());
        }

        // Convert DTO to entity
        ApplicationEntity application = applicationMapper.toEntity(request);
        application.setUser(user);

        // Save application
        ApplicationEntity savedApp = applicationRepository.save(application);

        // Create initial status history entry
        StatusHistory history = new StatusHistory();
        history.setApplication(savedApp);
        history.setOldStatus(null);
        history.setNewStatus(savedApp.getStatus());
        history.setChangedAt(LocalDateTime.now());
        statusHistoryRepository.save(history);

        log.info("Application created with ID: {}", savedApp.getId());

        return applicationMapper.toResponseDTO(savedApp);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponseDTO getApplicationById(Long id) {
        log.debug("Fetching application with ID: {}", id);

        ApplicationEntity application = applicationRepository
                .findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        return applicationMapper.toResponseDTO(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getApplicationsByUserId(Long userId) {
        log.debug("Fetching applications for user ID: {}", userId);

        return applicationRepository.findByUserId(userId)
                .stream()
                .map(applicationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponseDTO> getApplicationsByUserId(
            Long userId, Pageable pageable) {
        log.debug("Fetching paginated applications for user ID: {}", userId);

        return applicationRepository.findByUserId(userId, pageable)
                .map(applicationMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status)
                .stream()
                .map(applicationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationResponseDTO updateApplication(
            Long id, ApplicationCreateRequest request) {
        log.info("Updating application with ID: {}", id);

        ApplicationEntity application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        // Update fields
        application.setCompanyName(request.getCompanyName());
        application.setPosition(request.getPosition());
        application.setSalary(request.getSalary());
        application.setJobBoardSource(request.getJobBoardSource());
        application.setJobUrl(request.getJobUrl());
        application.setNotes(request.getNotes());

        // Status change handled separately
        if (!application.getStatus().equals(request.getStatus())) {
            updateApplicationStatus(id, request.getStatus());
        }

        ApplicationEntity updated = applicationRepository.save(application);
        return applicationMapper.toResponseDTO(updated);
    }

    @Override
    public ApplicationResponseDTO updateApplicationStatus(
            Long id, ApplicationStatus newStatus) {
        log.info("Updating status for application ID: {} to {}", id, newStatus);

        ApplicationEntity application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        ApplicationStatus oldStatus = application.getStatus();

        if (oldStatus.equals(newStatus)) {
            log.debug("Status unchanged, skipping update");
            return applicationMapper.toResponseDTO(application);
        }

        // Update status
        application.setStatus(newStatus);
        ApplicationEntity updated = applicationRepository.save(application);

        // Record status change
        StatusHistory history = new StatusHistory();
        history.setApplication(updated);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedAt(LocalDateTime.now());
        statusHistoryRepository.save(history);

        log.info("Status updated successfully");
        return applicationMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteApplication(Long id) {
        log.info("Deleting application with ID: {}", id);

        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Application", "id", id);
        }

        // Cascade delete handles reminders, notes, history
        applicationRepository.deleteById(id);
        log.info("Application deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> searchByCompany(String keyword) {
        return applicationRepository.findByCompanyNameContainingIgnoreCase(keyword)
                .stream()
                .map(applicationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}