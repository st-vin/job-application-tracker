package org.alvin.jobapplicationtracker.dto.response;

import lombok.Data;
import org.alvin.jobapplicationtracker.entity.ApplicationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ApplicationResponseDTO {
    private Long id;
    private String companyName;
    private String position;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private Double salary;
    private String jobBoardSource;
    private String jobUrl;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested user info (limited)
    private UserSummaryDTO user;

    // Counts only, not full lists
    private Integer reminderCount;
    private Integer interviewNoteCount;
    private Integer statusHistoryCount;
}