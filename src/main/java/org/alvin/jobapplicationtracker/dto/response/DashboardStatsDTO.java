package org.alvin.jobapplicationtracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalApplications;
    private Map<String, Long> byStatus;
    private Double averageSalary;
    private long last30DaysSubmissions;
}


