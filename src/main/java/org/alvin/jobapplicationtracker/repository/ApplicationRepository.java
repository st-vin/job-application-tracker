package org.alvin.jobapplicationtracker.repository;

import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.alvin.jobapplicationtracker.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

    /**
     * Find application by ID with user details
     * Prevents N+1 query problem by fetching user eagerly
     */
    @Query("SELECT a FROM ApplicationEntity a LEFT JOIN FETCH a.user WHERE a.id = :id")
    Optional<ApplicationEntity> findByIdWithUser(@Param("id") Long id);

    /**
     * Find applications by status with pagination
     * @param status The application status
     * @param pageable Pagination information
     * @return Page of applications
     */
    Page<ApplicationEntity> findByStatus(ApplicationStatus status, Pageable pageable);

    /**
     * Find applications by status (non-paginated)
     * @param status The application status
     * @return List of applications
     */
    List<ApplicationEntity> findByStatus(ApplicationStatus status);

    /**
     * Find applications by company name (case-insensitive)
     * @param companyName The company name
     * @return List of applications
     */
    @Query("SELECT a FROM ApplicationEntity a WHERE LOWER(a.companyName) = LOWER(:companyName)")
    List<ApplicationEntity> findByCompanyNameIgnoreCase(@Param("companyName") String companyName);

    /**
     * Find applications by company name containing keyword (case-insensitive)
     * @param keyword The search keyword
     * @return List of applications
     */
    @Query("SELECT a FROM ApplicationEntity a WHERE LOWER(a.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ApplicationEntity> findByCompanyNameContainingIgnoreCase(@Param("keyword") String keyword);

    /**
     * Find applications by company name containing keyword (case-insensitive) for a specific user
     * @param keyword The search keyword
     * @param userId The user ID to scope results
     * @return List of applications for the user matching the keyword
     */
    @Query("SELECT a FROM ApplicationEntity a WHERE a.user.id = :userId AND LOWER(a.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ApplicationEntity> findByCompanyNameContainingIgnoreCaseAndUserId(@Param("keyword") String keyword,
                                                                           @Param("userId") Long userId);

    /**
     * Find all applications ordered by salary descending with pagination
     * @param pageable Pagination information
     * @return Page of applications sorted by salary desc
     */
    Page<ApplicationEntity> findAllByOrderBySalaryDesc(Pageable pageable);

    /**
     * Find all applications ordered by salary ascending with pagination
     * @param pageable Pagination information
     * @return Page of applications sorted by salary asc
     */
    Page<ApplicationEntity> findAllByOrderBySalaryAsc(Pageable pageable);

    /**
     * Find applications by user ID
     * @param userId The user ID
     * @return List of applications for the user
     */
    @Query("SELECT a FROM ApplicationEntity a WHERE a.user.id = :userId")
    List<ApplicationEntity> findByUserId(@Param("userId") Long userId);

    /**
     * Find applications by user ID with pagination
     * @param userId The user ID
     * @param pageable Pagination information
     * @return Page of applications for the user
     */
    @Query("SELECT a FROM ApplicationEntity a WHERE a.user.id = :userId")
    Page<ApplicationEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find applications within salary range
     * @param minSalary Minimum salary
     * @param maxSalary Maximum salary
     * @return List of applications within range
     */
    @Query("SELECT a FROM ApplicationEntity a WHERE a.salary BETWEEN :minSalary AND :maxSalary ORDER BY a.salary DESC")
    List<ApplicationEntity> findBySalaryRange(@Param("minSalary") Double minSalary,
                                              @Param("maxSalary") Double maxSalary);

    /**
     * Find applications by status and user ID
     * @param status The application status
     * @param userId The user ID
     * @return List of applications matching criteria
     */
    @Query("SELECT a FROM ApplicationEntity a WHERE a.status = :status AND a.user.id = :userId")
    List<ApplicationEntity> findByStatusAndUserId(@Param("status") ApplicationStatus status,
                                                  @Param("userId") Long userId);

    /**
     * Find applications created between dates
     * @param startDate Start date
     * @param endDate End date
     * @return List of applications created in range
     */
    @Query("SELECT a FROM ApplicationEntity a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<ApplicationEntity> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Find applications updated after a certain date
     * @param date The cutoff date
     * @return List of recently updated applications
     */
    @Query("SELECT a FROM ApplicationEntity a WHERE a.updatedAt >= :date ORDER BY a.updatedAt DESC")
    List<ApplicationEntity> findRecentlyUpdated(@Param("date") LocalDateTime date);

    /**
     * Count applications by status
     * @param status The application status
     * @return Count of applications
     */
    Long countByStatus(ApplicationStatus status);

    /**
     * Count applications by user ID
     * @param userId The user ID
     * @return Count of applications for the user
     */
    @Query("SELECT COUNT(a) FROM ApplicationEntity a WHERE a.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * Check if application exists for user and company
     * Useful to prevent duplicate applications
     * @param userId The user ID
     * @param companyName The company name
     * @return true if application exists
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM ApplicationEntity a " +
            "WHERE a.user.id = :userId AND LOWER(a.companyName) = LOWER(:companyName)")
    boolean existsByUserIdAndCompanyName(@Param("userId") Long userId,
                                         @Param("companyName") String companyName);

    /**
     * Find top N highest paying applications
     * @param pageable Pagination (use PageRequest.of(0, n) for top N)
     * @return List of highest paying applications
     */
    @Query("SELECT a FROM ApplicationEntity a ORDER BY a.salary DESC")
    List<ApplicationEntity> findTopBySalary(Pageable pageable);

    /**
     * Delete applications by user ID
     * Useful for GDPR compliance - when user requests data deletion
     * @param userId The user ID
     */
    @Query("DELETE FROM ApplicationEntity a WHERE a.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}