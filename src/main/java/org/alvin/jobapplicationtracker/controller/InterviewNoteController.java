package org.alvin.jobapplicationtracker.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.alvin.jobapplicationtracker.entity.InterviewNote;
import org.alvin.jobapplicationtracker.entity.Role;
import org.alvin.jobapplicationtracker.exception.ResourceNotFoundException;
import org.alvin.jobapplicationtracker.repository.ApplicationRepository;
import org.alvin.jobapplicationtracker.repository.InterviewNoteRepository;
import org.alvin.jobapplicationtracker.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Slf4j
public class InterviewNoteController {

    private final InterviewNoteRepository interviewNoteRepository;
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
    public ResponseEntity<List<InterviewNote>> listByApplication(@PathVariable Long applicationId) {
        ApplicationEntity app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
        assertOwnershipOrAdmin(app);
        List<InterviewNote> notes = interviewNoteRepository.findByApplicationIdWithDetails(applicationId);
        return ResponseEntity.ok(notes);
    }

    @PostMapping("/application/{applicationId}")
    public ResponseEntity<InterviewNote> create(@PathVariable Long applicationId, @Valid @RequestBody CreateNoteRequest request) {
        log.info("Creating interview note for application {}", applicationId);
        ApplicationEntity app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
        assertOwnershipOrAdmin(app);

        InterviewNote note = new InterviewNote();
        note.setApplication(app);
        note.setInterviewStage(request.getInterviewStage());
        note.setNote(request.getNote());
        InterviewNote saved = interviewNoteRepository.save(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        InterviewNote note = interviewNoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewNote", "id", id));
        assertOwnershipOrAdmin(note.getApplication());
        interviewNoteRepository.delete(note);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class CreateNoteRequest {
        @NotBlank
        private String note;
        @NotNull
        private String interviewStage;
    }
}


