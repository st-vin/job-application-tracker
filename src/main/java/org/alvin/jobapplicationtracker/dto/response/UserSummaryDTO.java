package org.alvin.jobapplicationtracker.dto.response;

import lombok.Data;

@Data
public class UserSummaryDTO {
    private Long id;
    private String firstName;
    private String lastName;
    // Minimal user info for nested responses
}