package org.alvin.jobapplicationtracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvin.jobapplicationtracker.dto.response.DashboardStatsDTO;
import org.alvin.jobapplicationtracker.entity.ApplicationStatus;
import org.alvin.jobapplicationtracker.entity.Role;
import org.alvin.jobapplicationtracker.repository.ApplicationRepository;
import org.alvin.jobapplicationtracker.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final ApplicationRepository applicationRepository;
    private final SecurityUtil securityUtil;

    private boolean isAdmin() {
        return securityUtil.getCurrentUser().getRole() == Role.ADMIN;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        Long scopeUserId = isAdmin() ? null : securityUtil.getCurrentUserId();

        long total;
        if (scopeUserId == null) {
            total = applicationRepository.count();
        } else {
            total = applicationRepository.countByUserId(scopeUserId);
        }

        Map<String, Long> byStatus = new java.util.HashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            long count = (scopeUserId == null)
                    ? applicationRepository.countByStatus(status)
                    : applicationRepository.findByStatusAndUserId(status, scopeUserId).size();
            byStatus.put(status.name(), count);
        }

        // Average salary (naive, small dataset). For larger sets, use JPQL AVG query.
        Double avgSalary;
        if (scopeUserId == null) {
            avgSalary = applicationRepository.findAll().stream()
                    .mapToDouble(a -> a.getSalary())
                    .filter(s -> s > 0)
                    .average().orElse(Double.NaN);
        } else {
            avgSalary = applicationRepository.findByUserId(scopeUserId).stream()
                    .mapToDouble(a -> a.getSalary())
                    .filter(s -> s > 0)
                    .average().orElse(Double.NaN);
        }

        // Submissions last 30 days
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        long last30 = (scopeUserId == null)
                ? applicationRepository.findByCreatedAtBetween(thirtyDaysAgo, now).size()
                : applicationRepository.findByUserId(scopeUserId).stream()
                    .filter(a -> a.getCreatedAt() != null && !a.getCreatedAt().isBefore(thirtyDaysAgo))
                    .count();

        DashboardStatsDTO dto = DashboardStatsDTO.builder()
                .totalApplications(total)
                .byStatus(byStatus)
                .averageSalary(Double.isNaN(avgSalary) ? null : avgSalary)
                .last30DaysSubmissions(last30)
                .build();

        return ResponseEntity.ok(dto);
    }
}


