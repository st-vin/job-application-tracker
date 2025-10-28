package org.alvin.jobapplicationtracker.repository;

import org.alvin.jobapplicationtracker.entity.UserEntity;
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
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Find user by email (case-insensitive)
     * Critical for login and authentication
     * @param email The user's email
     * @return Optional user entity
     */
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<UserEntity> findByEmailIgnoreCase(@Param("email") String email);

    /**
     * Find user by email (exact match)
     * @param email The user's email
     * @return Optional user entity
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Find users by first name (case-insensitive)
     * @param firstName The first name to search
     * @return List of matching users
     */
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.firstName) = LOWER(:firstName)")
    List<UserEntity> findByFirstNameIgnoreCase(@Param("firstName") String firstName);

    /**
     * Find users by last name (case-insensitive)
     * @param lastName The last name to search
     * @return List of matching users
     */
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.lastName) = LOWER(:lastName)")
    List<UserEntity> findByLastNameIgnoreCase(@Param("lastName") String lastName);

    /**
     * Find users by full name (case-insensitive)
     * @param firstName First name
     * @param lastName Last name
     * @return Optional user entity
     */
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.firstName) = LOWER(:firstName) " +
            "AND LOWER(u.lastName) = LOWER(:lastName)")
    Optional<UserEntity> findByFullNameIgnoreCase(@Param("firstName") String firstName,
                                                  @Param("lastName") String lastName);

    /**
     * Search users by name containing keyword (case-insensitive)
     * Searches both first name and last name
     * @param keyword The search keyword
     * @return List of matching users
     */
    @Query("SELECT u FROM UserEntity u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<UserEntity> searchByNameContaining(@Param("keyword") String keyword);

    /**
     * Search users with pagination
     * @param keyword The search keyword
     * @param pageable Pagination information
     * @return Page of matching users
     */
    @Query("SELECT u FROM UserEntity u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<UserEntity> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find user with their applications (eager fetch)
     * Prevents N+1 query problem
     * @param userId The user ID
     * @return Optional user with applications loaded
     */
    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.applications WHERE u.id = :userId")
    Optional<UserEntity> findByIdWithApplications(@Param("userId") Long userId);

    /**
     * Find users with application count
     * @return List of users with their application counts
     */
    @Query("SELECT u FROM UserEntity u LEFT JOIN u.applications a GROUP BY u ORDER BY COUNT(a) DESC")
    List<UserEntity> findUsersOrderByApplicationCount();

    /**
     * Find active users (users with at least one application)
     * @return List of active users
     */
    @Query("SELECT DISTINCT u FROM UserEntity u INNER JOIN u.applications")
    List<UserEntity> findActiveUsers();

    /**
     * Find inactive users (users with no applications)
     * @return List of inactive users
     */
    @Query("SELECT u FROM UserEntity u WHERE u.applications IS EMPTY")
    List<UserEntity> findInactiveUsers();

    /**
     * Check if email already exists (case-insensitive)
     * Critical for preventing duplicate accounts
     * @param email The email to check
     * @return true if email exists
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u " +
            "WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    /**
     * Check if email exists excluding specific user ID
     * Useful for update operations to prevent duplicate emails
     * @param email The email to check
     * @param userId The user ID to exclude from check
     * @return true if email exists for another user
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u " +
            "WHERE LOWER(u.email) = LOWER(:email) AND u.id != :userId")
    boolean existsByEmailIgnoreCaseAndIdNot(@Param("email") String email, @Param("userId") Long userId);

    /**
     * Count users registered after a certain date
     * @param date The cutoff date
     * @return Count of new users
     */
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.createdAt >= :date")
    Long countNewUsers(@Param("date") LocalDateTime date);

    /**
     * Find recently registered users
     * @param date The cutoff date
     * @return List of recent users
     */
    @Query("SELECT u FROM UserEntity u WHERE u.createdAt >= :date ORDER BY u.createdAt DESC")
    List<UserEntity> findRecentlyRegistered(@Param("date") LocalDateTime date);

    /**
     * Find users registered between dates
     * @param startDate Start date
     * @param endDate End date
     * @return List of users registered in range
     */
    @Query("SELECT u FROM UserEntity u WHERE u.createdAt BETWEEN :startDate AND :endDate ORDER BY u.createdAt DESC")
    List<UserEntity> findByRegistrationDateBetween(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Update user's last login timestamp
     * @param userId The user ID
     * @param timestamp The login timestamp
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.updatedAt = :timestamp WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("timestamp") LocalDateTime timestamp);

    /**
     * Update user's password
     * Note: Password should be hashed before calling this method
     * @param userId The user ID
     * @param hashedPassword The new hashed password
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.password = :hashedPassword, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updatePassword(@Param("userId") Long userId, @Param("hashedPassword") String hashedPassword);

    /**
     * Update user's email
     * @param userId The user ID
     * @param newEmail The new email address
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.email = :newEmail, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateEmail(@Param("userId") Long userId, @Param("newEmail") String newEmail);

    /**
     * Soft delete user by marking as inactive (if you implement soft delete)
     * Note: This assumes you have an 'active' field in UserEntity
     * @param userId The user ID
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void deactivateUser(@Param("userId") Long userId);

    /**
     * Delete user and all associated data (for GDPR compliance)
     * Note: This will cascade delete applications and reminders if configured
     * @param userId The user ID
     */
    @Modifying
    @Query("DELETE FROM UserEntity u WHERE u.id = :userId")
    void deleteUserById(@Param("userId") Long userId);

    /**
     * Get user statistics
     * Returns user with application count
     * @param userId The user ID
     * @return Array with [UserEntity, applicationCount]
     */
    @Query("""
    SELECT u, COUNT(a)
    FROM UserEntity u
    LEFT JOIN u.applications a
    WHERE u.id = :userId
    GROUP BY u
""")
    List<Object[]> getUserStatistics(@Param("userId") Long userId);


    /**
     * Find users with more than N applications
     * @param minApplications Minimum number of applications
     * @return List of users meeting criteria
     */
    @Query("SELECT u FROM UserEntity u LEFT JOIN u.applications a " +
            "GROUP BY u HAVING COUNT(a) > :minApplications")
    List<UserEntity> findUsersWithMoreThanNApplications(@Param("minApplications") Long minApplications);

    /**
     * Verify user credentials (for authentication)
     * WARNING: In production, never store plain text passwords
     * Use BCrypt or similar hashing algorithm
     * @param email User's email
     * @param password User's password (should be hashed)
     * @return Optional user if credentials match
     */
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.email) = LOWER(:email) AND u.password = :password")
    Optional<UserEntity> findByEmailAndPassword(@Param("email") String email,
                                                @Param("password") String password);

    /**
     * Get all users with pagination
     * @param pageable Pagination information
     * @return Page of users
     */
    Page<UserEntity> findAll(Pageable pageable);

    /**
     * Find users ordered by creation date (newest first)
     * @param pageable Pagination information
     * @return Page of users sorted by creation date
     */
    Page<UserEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Count total users
     * @return Total number of users
     */
    @Override
    long count();
}