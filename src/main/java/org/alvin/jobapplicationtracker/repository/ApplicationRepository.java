package org.alvin.jobapplicationtracker.repository;

import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.alvin.jobapplicationtracker.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

/*    // find applications by id
    Optional<ApplicationEntity> findById(Long id);
    // find applications by status
    List<ApplicationEntity> findByStatus(ApplicationStatus status);

    // find applications by company name
    List<ApplicationEntity> findByCompanyName(String companyName);

   // sort applications by salary descending
    List<ApplicationEntity> OrderBySalaryDesc(Double salary);

    // sort applications by salary ascending
    List<ApplicationEntity> OrderBySalaryAsc(Double salary);*/
}
