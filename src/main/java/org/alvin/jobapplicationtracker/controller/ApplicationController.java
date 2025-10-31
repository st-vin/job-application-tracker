package org.alvin.jobapplicationtracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvin.jobapplicationtracker.dto.request.ApplicationCreateRequest;
import org.alvin.jobapplicationtracker.dto.response.ApplicationResponseDTO;
import org.alvin.jobapplicationtracker.entity.ApplicationStatus;
import org.alvin.jobapplicationtracker.service.ApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Create new application
     * POST /api/applications?userId=1
     */
    @PostMapping
    public ResponseEntity<ApplicationResponseDTO> createApplication(
            @Valid @RequestBody ApplicationCreateRequest request,
            @RequestParam Long userId) {
        log.info("Create application request for user ID: {}", userId);

        ApplicationResponseDTO application =
                applicationService.createApplication(request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(application);
    }

    /**
     * Get application by ID
     * GET /api/applications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDTO> getApplicationById(
            @PathVariable Long id) {
        log.debug("Request to get application with ID: {}", id);

        ApplicationResponseDTO application =
                applicationService.getApplicationById(id);

        return ResponseEntity.ok(application);
    }

    /**
     * Get all applications for a user
     * GET /api/applications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsByUser(
            @PathVariable Long userId) {
        log.debug("Request to get applications for user ID: {}", userId);

        List<ApplicationResponseDTO> applications =
                applicationService.getApplicationsByUserId(userId);

        return ResponseEntity.ok(applications);
    }

    /**
     * Get paginated applications for a user
     * GET /api/applications/user/{userId}/paginated?page=0&size=10
     */
    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<Page<ApplicationResponseDTO>> getApplicationsByUserPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("Request for paginated applications - user: {}, page: {}, size: {}",
                userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ApplicationResponseDTO> applications =
                applicationService.getApplicationsByUserId(userId, pageable);

        return ResponseEntity.ok(applications);
    }

    /**
     * Get applications by status
     * GET /api/applications/status/APPLIED
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsByStatus(
            @PathVariable ApplicationStatus status) {
        log.debug("Request to get applications with status: {}", status);

        List<ApplicationResponseDTO> applications =
                applicationService.getApplicationsByStatus(status);

        return ResponseEntity.ok(applications);
    }

    /**
     * Update application
     * PUT /api/applications/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponseDTO> updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationCreateRequest request) {
        log.info("Update request for application ID: {}", id);

        ApplicationResponseDTO application =
                applicationService.updateApplication(id, request);

        return ResponseEntity.ok(application);
    }

    /**
     * Update application status
     * PATCH /api/applications/{id}/status?status=PHONE_SCREEN
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponseDTO> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        log.info("Status update request for application ID: {} to {}", id, status);

        ApplicationResponseDTO application =
                applicationService.updateApplicationStatus(id, status);

        return ResponseEntity.ok(application);
    }

    /**
     * Delete application
     * DELETE /api/applications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        log.info("Delete request for application ID: {}", id);

        applicationService.deleteApplication(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Search applications by company name
     * GET /api/applications/search?keyword=google
     */
    @GetMapping("/search")
    public ResponseEntity<List<ApplicationResponseDTO>> searchApplications(
            @RequestParam String keyword) {
        log.debug("Search request with keyword: {}", keyword);

        List<ApplicationResponseDTO> applications =
                applicationService.searchByCompany(keyword);

        return ResponseEntity.ok(applications);
    }
}