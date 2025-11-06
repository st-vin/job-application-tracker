package org.alvin.jobapplicationtracker.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.alvin.jobapplicationtracker.entity.Role;
import org.alvin.jobapplicationtracker.entity.Reminder;
import org.alvin.jobapplicationtracker.exception.ResourceNotFoundException;
import org.alvin.jobapplicationtracker.repository.ApplicationRepository;
import org.alvin.jobapplicationtracker.repository.ReminderRepository;
import org.alvin.jobapplicationtracker.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@Slf4j
public class ReminderController {

    private final ReminderRepository reminderRepository;
    private final ApplicationRepository applicationRepository;
    private final SecurityUtil securityUtil;

    private boolean isAdmin() {
        return securityUtil.getCurrentUser().getRole() == Role.ADMIN;
    }

    private void assertOwnershipOrAdmin(ApplicationEntity application) {
        if (isAdmin()) return;
        Long ownerId = application.getUser() != null ? application.getUser().getId() : null;
        if (ownerId == null || !ownerId.equals(securityUtil.getCurrentUserId())) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to access this resource");
        }
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<Reminder>> listByApplication(@PathVariable Long applicationId) {
        ApplicationEntity app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
        assertOwnershipOrAdmin(app);
        List<Reminder> reminders = reminderRepository.findByApplicationId(applicationId);
        return ResponseEntity.ok(reminders);
    }

    @PostMapping("/application/{applicationId}")
    public ResponseEntity<Reminder> create(@PathVariable Long applicationId, @Valid @RequestBody CreateReminderRequest request) {
        log.info("Creating reminder for application {}", applicationId);
        ApplicationEntity app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
        assertOwnershipOrAdmin(app);

        Reminder reminder = new Reminder();
        reminder.setApplication(app);
        reminder.setReminderDate(request.getReminderDate());
        reminder.setReminderMessage(request.getReminderMessage());
        reminder.setSent(false);
        Reminder saved = reminderRepository.save(reminder);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));
        assertOwnershipOrAdmin(reminder.getApplication());
        reminderRepository.delete(reminder);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class CreateReminderRequest {
        @NotNull
        private LocalDateTime reminderDate;
        @NotBlank
        private String reminderMessage;
    }
}


