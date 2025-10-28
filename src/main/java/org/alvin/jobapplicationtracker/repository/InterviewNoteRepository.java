package org.alvin.jobapplicationtracker.repository;

import org.alvin.jobapplicationtracker.entity.InterviewNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for InterviewNote entity
 * Manages interview notes and feedback for job applications
 */
@Repository
public interface InterviewNoteRepository extends JpaRepository<InterviewNote, Long> {

    /**
     * Find all interview notes for an application (chronologically ordered)
     * Essential for viewing interview history
     * @param applicationId The application ID
     * @return List of interview notes ordered by date
     */
    @Query("SELECT n FROM InterviewNote n WHERE n.application.id = :applicationId ORDER BY n.createdAt DESC")
    List<InterviewNote> findByApplicationIdOrderByDateDesc(@Param("applicationId") Long applicationId);

    /**
     * Find all interview notes for an application with application details
     * Prevents N+1 query problem
     * @param applicationId The application ID
     * @return List of interview notes with application details
     */
    @Query("SELECT n FROM InterviewNote n LEFT JOIN FETCH n.application WHERE n.application.id = :applicationId ORDER BY n.createdAt DESC")
    List<InterviewNote> findByApplicationIdWithDetails(@Param("applicationId") Long applicationId);

    /**
     * Find latest interview note for an application
     * Useful for getting most recent interview feedback
     * @param applicationId The application ID
     * @return Optional most recent interview note
     */
    @Query("SELECT n FROM InterviewNote n WHERE n.application.id = :applicationId ORDER BY n.createdAt DESC")
    Optional<InterviewNote> findLatestByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * Find interview notes for a user's applications
     * @param userId The user ID
     * @return List of interview notes for user's applications
     */
    @Query("SELECT n FROM InterviewNote n JOIN n.application a WHERE a.user.id = :userId ORDER BY n.createdAt DESC")
    List<InterviewNote> findByUserId(@Param("userId") Long userId);

    /**
     * Find upcoming interviews (scheduled in the future)
     * @param now Current timestamp
     * @return List of upcoming interview notes
     */
    @Query("SELECT n FROM InterviewNote n WHERE n.createdAt > :now ORDER BY n.createdAt ASC")
    List<InterviewNote> findUpcomingInterviews(@Param("now") LocalDateTime now);

    /**
     * Find past interviews
     * @param now Current timestamp
     * @return List of past interview notes
     */
    @Query("SELECT n FROM InterviewNote n WHERE n.createdAt <= :now ORDER BY n.createdAt DESC")
    List<InterviewNote> findPastInterviews(@Param("now") LocalDateTime now);

    /**
     * Find interviews by type (e.g., "Phone Screen", "Technical", "Behavioral")
     * @param interviewType The type of interview
     * @return List of interview notes of specified type
     */
    @Query("SELECT n FROM InterviewNote n WHERE LOWER(n.interviewStage) = LOWER(:interviewType) ORDER BY n.createdAt DESC")
    List<InterviewNote> findByInterviewTypeIgnoreCase(@Param("interviewType") String interviewType);

    /**
     * Find interviews scheduled between dates
     * Useful for calendar view and scheduling
     * @param startDate Start date
     * @param endDate End date
     * @return List of interviews in date range
     */
    @Query("SELECT n FROM InterviewNote n WHERE n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt ASC")
    List<InterviewNote> findBycreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Count interview notes for an application
     * @param applicationId The application ID
     * @return Count of interview notes
     */
    @Query("SELECT COUNT(n) FROM InterviewNote n WHERE n.application.id = :applicationId")
    Long countByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * Search interview notes by content (case-insensitive)
     * Useful for finding specific feedback or keywords
     * @param keyword The search keyword
     * @return List of matching interview notes
     */
    @Query("SELECT n FROM InterviewNote n WHERE LOWER(n.note) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(n.interviewStage) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY n.createdAt DESC")
    List<InterviewNote> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Find recent interview notes (within last N days)
     * @param date The cutoff date
     * @return List of recent interview notes
     */
    @Query("SELECT n FROM InterviewNote n WHERE n.createdAt >= :date ORDER BY n.createdAt DESC")
    List<InterviewNote> findRecentNotes(@Param("date") LocalDateTime date);

    /**
     * Delete all interview notes for an application
     * Used when deleting an application
     * @param applicationId The application ID
     */
    @Query("DELETE FROM InterviewNote n WHERE n.application.id = :applicationId")
    void deleteByApplicationId(@Param("applicationId") Long applicationId);
}