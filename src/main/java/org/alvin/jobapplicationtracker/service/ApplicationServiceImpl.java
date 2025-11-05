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
import org.alvin.jobapplicationtracker.util.SecurityUtil;
import org.alvin.jobapplicationtracker.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
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
    private final SecurityUtil securityUtil;

    private boolean isAdmin() {
        return securityUtil.getCurrentUser().getRole() == Role.ADMIN;
    }

    private void assertOwnershipOrAdmin(ApplicationEntity application) {
        if (isAdmin()) {
            return;
        }
        Long currentUserId = securityUtil.getCurrentUserId();
        Long ownerId = application.getUser() != null ? application.getUser().getId() : null;
        if (ownerId == null || !ownerId.equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
    }

    @Override
    public ApplicationResponseDTO createApplication(
            ApplicationCreateRequest request, Long userId) {
        log.info("Creating application for user ID: {}", userId);

        // Authorization: regular users can only create for themselves
        if (!isAdmin() && !securityUtil.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("You cannot create applications for another user");
        }

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

        // Authorization
        assertOwnershipOrAdmin(application);

        return applicationMapper.toResponseDTO(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getApplicationsByUserId(Long userId) {
        log.debug("Fetching applications for user ID: {}", userId);

        // Authorization: regular users can only access their own list
        if (!isAdmin() && !securityUtil.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("You cannot access another user's applications");
        }

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

        // Authorization: regular users can only access their own list
        if (!isAdmin() && !securityUtil.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("You cannot access another user's applications");
        }

        return applicationRepository.findByUserId(userId, pageable)
                .map(applicationMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getApplicationsByStatus(ApplicationStatus status) {
        if (isAdmin()) {
            return applicationRepository.findByStatus(status)
                    .stream()
                    .map(applicationMapper::toResponseDTO)
                    .collect(Collectors.toList());
        }
        Long currentUserId = securityUtil.getCurrentUserId();
        return applicationRepository.findByStatusAndUserId(status, currentUserId)
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

        // Authorization
        assertOwnershipOrAdmin(application);

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

        // Authorization
        assertOwnershipOrAdmin(application);

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

        ApplicationEntity application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        // Authorization
        assertOwnershipOrAdmin(application);

        // Cascade delete handles reminders, notes, history
        applicationRepository.deleteById(id);
        log.info("Application deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> searchByCompany(String keyword) {
        if (isAdmin()) {
            return applicationRepository.findByCompanyNameContainingIgnoreCase(keyword)
                    .stream()
                    .map(applicationMapper::toResponseDTO)
                    .collect(Collectors.toList());
        }
        Long currentUserId = securityUtil.getCurrentUserId();
        return applicationRepository.findByCompanyNameContainingIgnoreCaseAndUserId(keyword, currentUserId)
                .stream()
                .map(applicationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> filterApplications(
            ApplicationStatus status,
            Double minSalary,
            Double maxSalary,
            String keyword) {
        Long currentUserId = securityUtil.getCurrentUserId();
        boolean admin = isAdmin();

        Specification<ApplicationEntity> spec = Specification.where(null);

        if (!admin) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), currentUserId));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (minSalary != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("salary"), minSalary));
        }
        if (maxSalary != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("salary"), maxSalary));
        }
        if (keyword != null && !keyword.isBlank()) {
            String kw = "%" + keyword.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("companyName")), kw),
                    cb.like(cb.lower(root.get("position")), kw)
            ));
        }

        return applicationRepository.findAll(spec).stream()
                .map(applicationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}