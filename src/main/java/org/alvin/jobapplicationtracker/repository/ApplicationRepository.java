package org.alvin.jobapplicationtracker.repository;

import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
}
