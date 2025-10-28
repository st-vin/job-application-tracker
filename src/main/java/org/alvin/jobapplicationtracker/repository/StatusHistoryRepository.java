package org.alvin.jobapplicationtracker.repository;

import org.alvin.jobapplicationtracker.entity.ApplicationStatus;
import org.alvin.jobapplicationtracker.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for StatusHistory entity
 * Tracks status changes for job applications
 */
@Repository
public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {

    /**
     * Find all status history for an application (chronologically ordered)
     * Essential for viewing application timeline
     * @param applicationId The application ID
     * @return List of status history entries ordered by date
     */
    @Query("SELECT sh FROM StatusHistory sh WHERE sh.application.id = :applicationId ORDER BY sh.changedAt DESC")
    List<StatusHistory> findByApplicationIdOrderByChangedAtDesc(@Param("applicationId") Long applicationId);

    /**
     * Find all status history for an application with application details loaded
     * Prevents N+1 query problem
     * @param applicationId The application ID
     * @return List of status history with application details
     */
    @Query("SELECT sh FROM StatusHistory sh LEFT JOIN FETCH sh.application WHERE sh.application.id = :applicationId ORDER BY sh.changedAt DESC")
    List<StatusHistory> findByApplicationIdWithDetails(@Param("applicationId") Long applicationId);

    /**
     * Find latest status change for an application
     * Useful for getting current status with change date
     * @param applicationId The application ID
     * @return Optional most recent status history entry
     */
    @Query("SELECT sh FROM StatusHistory sh WHERE sh.application.id = :applicationId ORDER BY sh.changedAt DESC")
    Optional<StatusHistory> findLatestByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * Find all status changes for a specific status
     * Useful for analytics (e.g., how many applications reached OFFER stage)
     * @param status The application status
     * @return List of status history entries
     */
    @Query("SELECT sh FROM StatusHistory sh WHERE sh.newStatus = :status ORDER BY sh.changedAt DESC")
    List<StatusHistory> findByNewStatus(@Param("status") ApplicationStatus status);

    /**
     * Find status history for a user's applications
     * @param userId The user ID
     * @return List of status history for user's applications
     */
    @Query("SELECT sh FROM StatusHistory sh JOIN sh.application a WHERE a.user.id = :userId ORDER BY sh.changedAt DESC")
    List<StatusHistory> findByUserId(@Param("userId") Long userId);

    /**
     * Find recent status changes (within last N days)
     * @param date The cutoff date
     * @return List of recent status changes
     */
    @Query("SELECT sh FROM StatusHistory sh WHERE sh.changedAt >= :date ORDER BY sh.changedAt DESC")
    List<StatusHistory> findRecentChanges(@Param("date") LocalDateTime date);

    /**
     * Count status changes for an application
     * Useful for tracking how many times status changed
     * @param applicationId The application ID
     * @return Count of status changes
     */
    @Query("SELECT COUNT(sh) FROM StatusHistory sh WHERE sh.application.id = :applicationId")
    Long countByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * Check if application has specific status in history
     * @param applicationId The application ID
     * @param status The status to check
     * @return true if application ever had this status
     */
    @Query("SELECT CASE WHEN COUNT(sh) > 0 THEN true ELSE false END FROM StatusHistory sh " +
            "WHERE sh.application.id = :applicationId AND sh.newStatus = :status")
    boolean hasEverHadStatus(@Param("applicationId") Long applicationId,
                             @Param("status") ApplicationStatus status);

    /**
     * Find status changes between dates
     * Useful for reporting and analytics
     * @param startDate Start date
     * @param endDate End date
     * @return List of status changes in date range
     */
    @Query("SELECT sh FROM StatusHistory sh WHERE sh.changedAt BETWEEN :startDate AND :endDate ORDER BY sh.changedAt DESC")
    List<StatusHistory> findByChangedAtBetween(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    /**
     * Delete all status history for an application
     * Used when deleting an application
     * @param applicationId The application ID
     */
    @Query("DELETE FROM StatusHistory sh WHERE sh.application.id = :applicationId")
    void deleteByApplicationId(@Param("applicationId") Long applicationId);
}