package org.alvin.jobapplicationtracker.repository;

import org.alvin.jobapplicationtracker.entity.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    /**
     * Find unsent reminders that are due (before current time)
     * Critical for reminder notification system
     * @param now Current timestamp
     * @return List of unsent, due reminders
     */
    @Query("SELECT r FROM Reminder r LEFT JOIN FETCH r.application " +
            "WHERE r.isSent = false AND r.reminderDate <= :now ORDER BY r.reminderDate ASC")
    List<Reminder> findUnsentDueReminders(@Param("now") LocalDateTime now);

    /**
     * Find reminders by application ID with application details fetched
     * Prevents N+1 query problem
     * @param applicationId The application ID
     * @return List of reminders for the application
     */
    @Query("SELECT r FROM Reminder r LEFT JOIN FETCH r.application " +
            "WHERE r.application.id = :applicationId ORDER BY r.reminderDate ASC")
    List<Reminder> findByApplicationIdWithDetails(@Param("applicationId") Long applicationId);

    /**
     * Find reminders by application ID (simple version)
     * @param applicationId The application ID
     * @return List of reminders
     */
    @Query("SELECT r FROM Reminder r WHERE r.application.id = :applicationId")
    List<Reminder> findByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * Find reminder by ID with application details
     * @param id The reminder ID
     * @return Optional reminder with details
     */
    @Query("SELECT r FROM Reminder r LEFT JOIN FETCH r.application WHERE r.id = :id")
    Optional<Reminder> findByIdWithApplication(@Param("id") Long id);

    /**
     * Find reminders by user ID (through application relationship)
     * @param userId The user ID
     * @return List of reminders for user's applications
     */
    @Query("SELECT r FROM Reminder r JOIN r.application a WHERE a.user.id = :userId ORDER BY r.reminderDate ASC")
    List<Reminder> findByUserId(@Param("userId") Long userId);

    /**
     * Find reminders by user ID with pagination
     * @param userId The user ID
     * @param pageable Pagination information
     * @return Page of reminders for user's applications
     */
    @Query("SELECT r FROM Reminder r JOIN r.application a WHERE a.user.id = :userId")
    Page<Reminder> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find unsent reminders by user ID
     * @param userId The user ID
     * @return List of unsent reminders for user's applications
     */
    @Query("SELECT r FROM Reminder r JOIN r.application a " +
            "WHERE a.user.id = :userId AND r.isSent = false ORDER BY r.reminderDate ASC")
    List<Reminder> findUnsentByUserId(@Param("userId") Long userId);

    /**
     * Find reminders within date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of reminders in date range
     */
    @Query("SELECT r FROM Reminder r WHERE r.reminderDate BETWEEN :startDate AND :endDate ORDER BY r.reminderDate ASC")
    List<Reminder> findByReminderDateBetween(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    /**
     * Find upcoming reminders (unsent and in the future)
     * @param now Current timestamp
     * @return List of upcoming reminders
     */
    @Query("SELECT r FROM Reminder r WHERE r.isSent = false AND r.reminderDate > :now ORDER BY r.reminderDate ASC")
    List<Reminder> findUpcomingReminders(@Param("now") LocalDateTime now);

    /**
     * Find upcoming reminders with limit
     * @param now Current timestamp
     * @param pageable Pagination (use PageRequest.of(0, n) for top N)
     * @return List of next N upcoming reminders
     */
    @Query("SELECT r FROM Reminder r WHERE r.isSent = false AND r.reminderDate > :now ORDER BY r.reminderDate ASC")
    List<Reminder> findNextUpcomingReminders(@Param("now") LocalDateTime now, Pageable pageable);

    /**
     * Find overdue unsent reminders
     * @param now Current timestamp
     * @return List of overdue reminders
     */
    @Query("SELECT r FROM Reminder r WHERE r.isSent = false AND r.reminderDate < :now ORDER BY r.reminderDate ASC")
    List<Reminder> findOverdueReminders(@Param("now") LocalDateTime now);

    /**
     * Mark reminder as sent
     * More efficient than fetching, modifying, and saving
     * @param reminderId The reminder ID
     */
    @Modifying
    @Query("UPDATE Reminder r SET r.isSent = true WHERE r.id = :reminderId")
    void markAsSent(@Param("reminderId") Long reminderId);

    /**
     * Mark multiple reminders as sent
     * Batch operation for efficiency
     * @param reminderIds List of reminder IDs
     */
    @Modifying
    @Query("UPDATE Reminder r SET r.isSent = true WHERE r.id IN :reminderIds")
    void markMultipleAsSent(@Param("reminderIds") List<Long> reminderIds);

    /**
     * Mark all due reminders as sent
     * @param now Current timestamp
     */
    @Modifying
    @Query("UPDATE Reminder r SET r.isSent = true WHERE r.isSent = false AND r.reminderDate <= :now")
    int markAllDueAsSent(@Param("now") LocalDateTime now);

    /**
     * Count unsent reminders for a user
     * @param userId The user ID
     * @return Count of unsent reminders
     */
    @Query("SELECT COUNT(r) FROM Reminder r JOIN r.application a " +
            "WHERE a.user.id = :userId AND r.isSent = false")
    Long countUnsentByUserId(@Param("userId") Long userId);

    /**
     * Count due reminders (unsent and before now)
     * @param now Current timestamp
     * @return Count of due reminders
     */
    @Query("SELECT COUNT(r) FROM Reminder r WHERE r.isSent = false AND r.reminderDate <= :now")
    Long countDueReminders(@Param("now") LocalDateTime now);

    /**
     * Delete reminders by application ID
     * Useful for cascading deletes or cleanup
     * @param applicationId The application ID
     */
    @Modifying
    @Query("DELETE FROM Reminder r WHERE r.application.id = :applicationId")
    void deleteByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * Delete reminders by user ID
     * Useful for GDPR compliance
     * @param userId The user ID
     */
    @Modifying
    @Query("DELETE FROM Reminder r WHERE r.application.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * Delete old sent reminders (cleanup operation)
     * @param cutoffDate Date before which to delete
     */
    @Modifying
    @Query("DELETE FROM Reminder r WHERE r.isSent = true AND r.reminderDate < :cutoffDate")
    int deleteOldSentReminders(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Check if application has unsent reminders
     * @param applicationId The application ID
     * @return true if unsent reminders exist
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reminder r " +
            "WHERE r.application.id = :applicationId AND r.isSent = false")
    boolean hasUnsentReminders(@Param("applicationId") Long applicationId);
}