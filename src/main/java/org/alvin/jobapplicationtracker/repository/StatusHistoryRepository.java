package org.alvin.jobapplicationtracker.repository;

import org.alvin.jobapplicationtracker.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory,Long> {
}
