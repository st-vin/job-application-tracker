package org.alvin.jobapplicationtracker.repository;

import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusHistoryRepository extends JpaRepository<ApplicationEntity,Long> {
}
