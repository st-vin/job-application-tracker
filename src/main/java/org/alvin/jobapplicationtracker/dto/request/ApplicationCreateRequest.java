package org.alvin.jobapplicationtracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.alvin.jobapplicationtracker.entity.ApplicationStatus;

import java.time.LocalDate;

@Data
public class ApplicationCreateRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    private LocalDate appliedDate;

    @Positive(message = "Salary must be positive")
    private Double salary;

    private String jobBoardSource;
    private String jobUrl;
    private String notes;
}