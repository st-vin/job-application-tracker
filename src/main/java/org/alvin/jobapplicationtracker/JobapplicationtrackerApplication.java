package org.alvin.jobapplicationtracker;

import jakarta.transaction.Transactional;
import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.alvin.jobapplicationtracker.entity.Reminder;
import org.alvin.jobapplicationtracker.entity.UserEntity;
import org.alvin.jobapplicationtracker.entity.StatusHistory;
import org.alvin.jobapplicationtracker.entity.InterviewNote;
import org.alvin.jobapplicationtracker.repository.ApplicationRepository;
import org.alvin.jobapplicationtracker.repository.UserRepository;
import org.alvin.jobapplicationtracker.repository.ReminderRepository;
import org.alvin.jobapplicationtracker.repository.StatusHistoryRepository;
import org.alvin.jobapplicationtracker.repository.InterviewNoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.alvin.jobapplicationtracker.entity.ApplicationStatus.*;

@SpringBootApplication
public class JobapplicationtrackerApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(JobapplicationtrackerApplication.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private StatusHistoryRepository statusHistoryRepository;

    @Autowired
    private InterviewNoteRepository interviewNoteRepository;

    public static void main(String[] args) {
        SpringApplication.run(JobapplicationtrackerApplication.class, args);
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {

        // --- Create sample users ---
        UserEntity ryan = new UserEntity("Ryan", "Coogler", "ryancoogler@gmail.com", "nicepasswordryan");
        UserEntity ford = new UserEntity("Ford", "Coppola", "fordcoppola@email.com", "TheGodfather");
        UserEntity wim = new UserEntity("Wim", "Wenders", "wenderwim@hotmail.com", "perfectDays");

        userRepository.save(ryan);
        userRepository.save(ford);
        userRepository.save(wim);

        logger.info("✅ Created {} users", userRepository.count());

        // --- TEST USER REPOSITORY METHODS (Basic Tests) ---
        testUserRepositoryBasics(ryan, ford);

        // --- Create sample applications ---
        ApplicationEntity app1 = new ApplicationEntity(
                "Google", "Junior Engineer", OFFER, LocalDate.of(2024, 1, 15), 80000.0,
                "LinkedIn", "https://google.com/jobs", "Great company!",
                LocalDateTime.now().minusDays(30), LocalDateTime.now(), ryan
        );

        ApplicationEntity app2 = new ApplicationEntity(
                "Amazon", "Software Developer", TECHNICAL, LocalDate.of(2024, 1, 15), 100000.0,
                "Indeed", "https://amazon.com/jobs", "Excellent opportunity to learn and grow.",
                LocalDateTime.now().minusDays(15), LocalDateTime.now().plusDays(10), ford
        );

        ApplicationEntity app3 = new ApplicationEntity(
                "Microsoft", "Data Scientist", OFFER, LocalDate.of(2024, 1, 15), 120000.0,
                "Glassdoor", "https://microsoft.com/jobs", "Innovative projects.",
                LocalDateTime.now().minusDays(20), LocalDateTime.now().plusDays(5), wim
        );

        ApplicationEntity app4 = new ApplicationEntity(
                "Safaricom", "Senior Security Analyst", APPLIED, LocalDate.of(2024, 1, 15), 220000.0,
                "BrightMonday", "https://safaricom.com/jobs", "Intensive analysis.",
                LocalDateTime.now().minusDays(20), LocalDateTime.now().plusDays(5), ryan
        );

        ApplicationEntity app5 = new ApplicationEntity(
                "Google", "Senior Backend Engineer", TECHNICAL, LocalDate.of(2024, 1, 15), 150000.0,
                "LinkedIn", "https://google.com/careers", "Amazing benefits!",
                LocalDateTime.now().minusDays(5), LocalDateTime.now(), ford
        );

        applicationRepository.save(app1);
        applicationRepository.save(app2);
        applicationRepository.save(app3);
        applicationRepository.save(app4);
        applicationRepository.save(app5);

        logger.info("✅ Created {} applications", applicationRepository.count());

        // --- Create sample reminders ---
        Reminder reminder1 = new Reminder();
        reminder1.setApplication(app1);
        reminder1.setReminderDate(LocalDateTime.now().plusDays(2));
        reminder1.setReminderMessage("Follow up with Google recruiter");
        reminder1.setSent(false);

        Reminder reminder2 = new Reminder();
        reminder2.setApplication(app2);
        reminder2.setReminderDate(LocalDateTime.now().minusDays(1));
        reminder2.setReminderMessage("Prepare for Amazon technical test");
        reminder2.setSent(false);

        Reminder reminder3 = new Reminder();
        reminder3.setApplication(app3);
        reminder3.setReminderDate(LocalDateTime.now().minusDays(4));
        reminder3.setReminderMessage("Send thank you email to Microsoft");
        reminder3.setSent(false);

        Reminder reminder4 = new Reminder();
                reminder4.setApplication(app4);
                reminder4.setReminderDate(LocalDateTime.now().minusDays(3));
                reminder4.setReminderMessage("Check Safaricom application status");
                reminder4.setSent(false);


        Reminder reminder5 = new Reminder();
        reminder5.setApplication(app5);
        reminder5.setReminderDate(LocalDateTime.now().minusDays(5));
        reminder5.setReminderMessage("Google interview prep");
        reminder5.setSent(false);

        reminderRepository.save(reminder1);
        reminderRepository.save(reminder2);
        reminderRepository.save(reminder3);
        reminderRepository.save(reminder4);
        reminderRepository.save(reminder5);

        logger.info("✅ Created {} reminders", reminderRepository.count());

        // --- Create status history entries ---
        StatusHistory history1 = new StatusHistory(app1, null, OFFER, LocalDateTime.now().minusDays(25));
        StatusHistory history2 = new StatusHistory(app2, APPLIED, TECHNICAL, LocalDateTime.now().minusDays(10));
        StatusHistory history3 = new StatusHistory(app3, TECHNICAL, OFFER, LocalDateTime.now().minusDays(15));
        StatusHistory history4 = new StatusHistory(app4, null, APPLIED, LocalDateTime.now().minusDays(20));

        statusHistoryRepository.save(history1);
        statusHistoryRepository.save(history2);
        statusHistoryRepository.save(history3);
        statusHistoryRepository.save(history4);

        logger.info("✅ Created {} status history entries", statusHistoryRepository.count());

        // --- Create interview notes ---
        InterviewNote note1 = new InterviewNote(
                "Great conversation, strong technical background. Moving to next round.",
                LocalDateTime.now().minusDays(28),
                "Phone Screen",
                app1
        );

        InterviewNote note2 = new InterviewNote(
                "Scheduled for coding challenge. Prepare data structures and algorithms.",
                LocalDateTime.now().plusDays(3),
                "Technical Interview",
                app2

        );

        InterviewNote note3 = new InterviewNote(
                "Excellent communication skills. Good culture fit. Team loved the candidate.",
                LocalDateTime.now().minusDays(18),
                "Behavioral Interview",
                app3

        );

        InterviewNote note4 = new InterviewNote(
                "Prepare scalability questions. Focus on distributed systems.",
                LocalDateTime.now().plusDays(5),
                "System Design",
                app5

        );

        interviewNoteRepository.save(note1);
        interviewNoteRepository.save(note2);
        interviewNoteRepository.save(note3);
        interviewNoteRepository.save(note4);

        logger.info("✅ Created {} interview notes", interviewNoteRepository.count());
        logger.info("\n" + "=".repeat(80));

        // --- TEST USER REPOSITORY METHODS (Comprehensive) ---
        testUserRepository(ryan.getId(), ford.getId());

        // --- TEST APPLICATION REPOSITORY METHODS ---
        testApplicationRepository(ryan.getId(), ford.getId());

        // --- TEST REMINDER REPOSITORY METHODS ---
        testReminderRepository(ryan.getId(), app1.getId());

        // --- TEST STATUS HISTORY REPOSITORY METHODS ---
        // testStatusHistoryRepository(app1.getId(), ryan.getId());

        // --- TEST INTERVIEW NOTE REPOSITORY METHODS ---
        // testInterviewNoteRepository(app1.getId(), ryan.getId());

        logger.info("\n" + "=".repeat(80));
        logger.info("🎉 All tests completed successfully!");
    }

    private void testUserRepositoryBasics(UserEntity ryan, UserEntity ford) {
        logger.info("\n👤 BASIC USER REPOSITORY TESTS");
        logger.info("=".repeat(80));

        // Test: Find by email
        Optional<UserEntity> foundUser = userRepository.findByEmailIgnoreCase("RYANCOOGLER@GMAIL.COM");
        foundUser.ifPresent(u -> logger.info("✓ Found user by email (case-insensitive): {} {}",
                u.getFirstName(), u.getLastName()));

        // Test: Check email exists
        boolean emailExists = userRepository.existsByEmailIgnoreCase("fordcoppola@email.com");
        logger.info("✓ Email existence check: {}", emailExists);
    }

    private void testUserRepository(Long ryanId, Long fordId) {
        logger.info("\n👤 COMPREHENSIVE USER REPOSITORY TESTS");
        logger.info("=".repeat(80));

        // Test 1: Find by email (case-insensitive)
        logger.info("\n1️⃣ Test: findByEmailIgnoreCase()");
        Optional<UserEntity> user = userRepository.findByEmailIgnoreCase("RYANCOOGLER@GMAIL.COM");
        user.ifPresent(u -> logger.info("   Found: {} {} ({})",
                u.getFirstName(), u.getLastName(), u.getEmail()));

        // Test 2: Find by first name
        logger.info("\n2️⃣ Test: findByFirstNameIgnoreCase()");
        List<UserEntity> ryans = userRepository.findByFirstNameIgnoreCase("ryan");
        logger.info("   Found {} users named Ryan", ryans.size());

        // Test 3: Find by full name
        logger.info("\n3️⃣ Test: findByFullNameIgnoreCase()");
        Optional<UserEntity> fullName = userRepository.findByFullNameIgnoreCase("Ford", "Coppola");
        fullName.ifPresent(u -> logger.info("   Found: {} {}", u.getFirstName(), u.getLastName()));

        // Test 4: Search by keyword
        logger.info("\n4️⃣ Test: searchByNameContaining()");
        List<UserEntity> searchResults = userRepository.searchByNameContaining("co");
        logger.info("   Found {} users with 'co' in their name:", searchResults.size());
        searchResults.forEach(u -> logger.info("   - {} {}", u.getFirstName(), u.getLastName()));

        // Test 5: Find with applications
        logger.info("\n5️⃣ Test: findByIdWithApplications()");
        Optional<UserEntity> withApps = userRepository.findByIdWithApplications(ryanId);
        withApps.ifPresent(u -> logger.info("   Ryan has {} applications (no N+1 query!)",
                u.getApplications().size()));

        // Test 6: Find active users
        logger.info("\n6️⃣ Test: findActiveUsers()");
        List<UserEntity> activeUsers = userRepository.findActiveUsers();
        logger.info("   Found {} active users (with applications)", activeUsers.size());

        // Test 7: Find inactive users
        logger.info("\n7️⃣ Test: findInactiveUsers()");
        List<UserEntity> inactiveUsers = userRepository.findInactiveUsers();
        logger.info("   Found {} inactive users (no applications)", inactiveUsers.size());

        // Test 8: Check email exists
        logger.info("\n8️⃣ Test: existsByEmailIgnoreCase()");
        boolean exists = userRepository.existsByEmailIgnoreCase("ryancoogler@gmail.com");
        logger.info("   Email exists: {}", exists);

        // Test 9: Check email exists excluding user
        logger.info("\n9️⃣ Test: existsByEmailIgnoreCaseAndIdNot()");
        boolean existsForOther = userRepository.existsByEmailIgnoreCaseAndIdNot(
                "ryancoogler@gmail.com", fordId);
        logger.info("   Email exists for another user: {}", existsForOther);

        // Test 10: Count new users
        logger.info("\n🔟 Test: countNewUsers()");
        Long newUserCount = userRepository.countNewUsers(LocalDateTime.now().minusDays(1));
        logger.info("   {} users registered in last 24 hours", newUserCount);

        // Test 11: Find recently registered
        logger.info("\n1️⃣1️⃣ Test: findRecentlyRegistered()");
        List<UserEntity> recentUsers = userRepository.findRecentlyRegistered(
                LocalDateTime.now().minusDays(7));
        logger.info("   {} users registered in last 7 days", recentUsers.size());

        // Test 12: Users ordered by application count
        logger.info("\n1️⃣2️⃣ Test: findUsersOrderByApplicationCount()");
        List<UserEntity> orderedUsers = userRepository.findUsersOrderByApplicationCount();
        logger.info("   Users by application count:");
        orderedUsers.forEach(u -> logger.info("   - {} {}: {} applications",
                u.getFirstName(), u.getLastName(), u.getApplications().size()));

        // Test 13: Find users with more than N applications
        logger.info("\n1️⃣3️⃣ Test: findUsersWithMoreThanNApplications()");
        List<UserEntity> powerUsers = userRepository.findUsersWithMoreThanNApplications(1L);
        logger.info("   Found {} users with more than 1 application", powerUsers.size());

        // Test 14: Get user statistics
        logger.info("\n1️⃣4️⃣ Test: getUserStatistics()");
        List<Object[]> statsList = userRepository.getUserStatistics(ryanId);

        if (statsList != null && !statsList.isEmpty()) {
            Object[] row = statsList.get(0); // first result row
            UserEntity u = (UserEntity) row[0];
            Long count = (Long) row[1];

            logger.info("   {} {} has {} applications",
                    u.getFirstName(), u.getLastName(), count);
        } else {
            logger.warn("   No statistics found for userId: {}", ryanId);
        }


        // Test 15: Update last login
        logger.info("\n1️⃣5️⃣ Test: updateLastLogin()");
        userRepository.updateLastLogin(ryanId, LocalDateTime.now());
        logger.info("   ✓ Updated Ryan's last login timestamp");

        // Test 16: Search with pagination
        logger.info("\n1️⃣6️⃣ Test: searchUsers() with pagination");
        Page<UserEntity> searchPage = userRepository.searchUsers("co", PageRequest.of(0, 2));
        logger.info("   Found {} results (Page 1 of {})",
                searchPage.getNumberOfElements(), searchPage.getTotalPages());

        // Test 17: Find all with pagination
        logger.info("\n1️⃣7️⃣ Test: findAll() with pagination");
        Page<UserEntity> allUsers = userRepository.findAll(PageRequest.of(0, 2));
        logger.info("   Retrieved {} of {} total users",
                allUsers.getNumberOfElements(), allUsers.getTotalElements());

        // Test 18: Verify credentials (demonstration only - use hashing in production!)
        logger.info("\n1️⃣8️⃣ Test: findByEmailAndPassword()");
        Optional<UserEntity> authenticated = userRepository.findByEmailAndPassword(
                "fordcoppola@email.com", "TheGodfather");
        authenticated.ifPresent(u -> logger.info("   ✓ Authentication successful for {}", u.getEmail()));

        logger.info("\n⚠️  WARNING: In production, ALWAYS hash passwords with BCrypt or similar!");
    }

    private void testApplicationRepository(Long ryanId, Long fordId) {
        logger.info("\n🔍 TESTING APPLICATION REPOSITORY METHODS");
        logger.info("=".repeat(80));

        // Test 1: Find by ID with user details
        logger.info("\n1️⃣ Test: findByIdWithUser()");
        Optional<ApplicationEntity> app = applicationRepository.findByIdWithUser(1L);
        app.ifPresent(a -> logger.info("   Found: {} at {} (User: {})",
                a.getPosition(), a.getCompanyName(), a.getUser().getFirstName()));

        // Test 2: Find by status with pagination
        logger.info("\n2️⃣ Test: findByStatus() with pagination");
        Pageable pageable = PageRequest.of(0, 2);
        Page<ApplicationEntity> offerApps = applicationRepository.findByStatus(OFFER, pageable);
        logger.info("   Found {} OFFER applications (Page 1 of {})",
                offerApps.getNumberOfElements(), offerApps.getTotalPages());
        offerApps.forEach(a -> logger.info("   - {}", a.getCompanyName()));

        // Test 3: Search by company name (case-insensitive)
        logger.info("\n3️⃣ Test: findByCompanyNameIgnoreCase()");
        List<ApplicationEntity> googleApps = applicationRepository.findByCompanyNameIgnoreCase("google");
        logger.info("   Found {} Google applications", googleApps.size());
        googleApps.forEach(a -> logger.info("   - {} (${:,.0f})", a.getPosition(), a.getSalary()));

        // Test 4: Search by company keyword
        logger.info("\n4️⃣ Test: findByCompanyNameContainingIgnoreCase()");
        List<ApplicationEntity> softwareApps = applicationRepository.findByCompanyNameContainingIgnoreCase("soft");
        logger.info("   Found {} applications matching 'soft'", softwareApps.size());

        // Test 5: Sort by salary descending with pagination
        logger.info("\n5️⃣ Test: findAllByOrderBySalaryDesc()");
        Page<ApplicationEntity> topSalaries = applicationRepository.findAllByOrderBySalaryDesc(PageRequest.of(0, 3));
        logger.info("   Top 3 highest paying positions:");
        topSalaries.forEach(a -> logger.info("   - {} at {}: ${:,.0f}",
                a.getPosition(), a.getCompanyName(), a.getSalary()));

        // Test 6: Find by user ID
        logger.info("\n6️⃣ Test: findByUserId()");
        List<ApplicationEntity> ryanApps = applicationRepository.findByUserId(ryanId);
        logger.info("   Ryan has {} applications", ryanApps.size());

        // Test 7: Find by salary range
        logger.info("\n7️⃣ Test: findBySalaryRange()");
        List<ApplicationEntity> midRange = applicationRepository.findBySalaryRange(90000.0, 150000.0);
        logger.info("   Found {} applications in $90k-$150k range", midRange.size());
        midRange.forEach(a -> logger.info("   - {} at {}: ${:,.0f}",
                a.getPosition(), a.getCompanyName(), a.getSalary()));

        // Test 8: Find by status and user
        logger.info("\n8️⃣ Test: findByStatusAndUserId()");
        List<ApplicationEntity> ryanOffers = applicationRepository.findByStatusAndUserId(OFFER, ryanId);
        logger.info("   Ryan has {} OFFER status applications", ryanOffers.size());

        // Test 9: Count by status
        logger.info("\n9️⃣ Test: countByStatus()");
        Long offerCount = applicationRepository.countByStatus(OFFER);
        Long technicalCount = applicationRepository.countByStatus(TECHNICAL);
        logger.info("   Status counts - OFFER: {}, TECHNICAL: {}", offerCount, technicalCount);

        // Test 10: Check duplicate applications
        logger.info("\n🔟 Test: existsByUserIdAndCompanyName()");
        boolean hasDuplicate = applicationRepository.existsByUserIdAndCompanyName(ryanId, "Google");
        logger.info("   Ryan already has Google application: {}", hasDuplicate);

        // Test 11: Find recently updated
        logger.info("\n1️⃣1️⃣ Test: findRecentlyUpdated()");
        List<ApplicationEntity> recent = applicationRepository.findRecentlyUpdated(LocalDateTime.now().minusDays(7));
        logger.info("   {} applications updated in last 7 days", recent.size());

        // Test 12: Find top N by salary
        logger.info("\n1️⃣2️⃣ Test: findTopBySalary()");
        List<ApplicationEntity> top2 = applicationRepository.findTopBySalary(PageRequest.of(0, 2));
        logger.info("   Top 2 highest salaries:");
        top2.forEach(a -> logger.info("   - ${:,.0f} - {}", a.getSalary(), a.getCompanyName()));
    }

    private void testReminderRepository(Long ryanId, Long app1Id) {
        logger.info("\n🔔 TESTING REMINDER REPOSITORY METHODS");
        logger.info("=".repeat(80));

        // Test 1: Find unsent due reminders
        logger.info("\n1️⃣ Test: findUnsentDueReminders()");
        List<Reminder> dueReminders = reminderRepository.findUnsentDueReminders(LocalDateTime.now());
        logger.info("   Found {} overdue reminders that need to be sent", dueReminders.size());
        dueReminders.forEach(r -> logger.info("   - {} (Due: {})",
                r.getReminderMessage(), r.getReminderDate()));

        // Test 2: Find by application ID with details
        logger.info("\n2️⃣ Test: findByApplicationIdWithDetails()");
        List<Reminder> appReminders = reminderRepository.findByApplicationIdWithDetails(app1Id);
        logger.info("   Found {} reminders for application #{}", appReminders.size(), app1Id);
        appReminders.forEach(r -> logger.info("   - {}", r.getReminderMessage()));

        // Test 3: Find by user ID
        logger.info("\n3️⃣ Test: findByUserId()");
        List<Reminder> userReminders = reminderRepository.findByUserId(ryanId);
        logger.info("   Ryan has {} total reminders", userReminders.size());

        // Test 4: Find unsent by user
        logger.info("\n4️⃣ Test: findUnsentByUserId()");
        List<Reminder> unsentForUser = reminderRepository.findUnsentByUserId(ryanId);
        logger.info("   Ryan has {} unsent reminders", unsentForUser.size());

        // Test 5: Find upcoming reminders
        logger.info("\n5️⃣ Test: findUpcomingReminders()");
        List<Reminder> upcoming = reminderRepository.findUpcomingReminders(LocalDateTime.now());
        logger.info("   {} upcoming reminders:", upcoming.size());
        upcoming.forEach(r -> logger.info("   - {} (Due in {} hours)",
                r.getReminderMessage(),
                java.time.Duration.between(LocalDateTime.now(), r.getReminderDate()).toHours()));

        // Test 6: Find next N upcoming
        logger.info("\n6️⃣ Test: findNextUpcomingReminders()");
        List<Reminder> next2 = reminderRepository.findNextUpcomingReminders(
                LocalDateTime.now(), PageRequest.of(0, 2));
        logger.info("   Next 2 upcoming reminders:");
        next2.forEach(r -> logger.info("   - {}", r.getReminderMessage()));

        // Test 7: Find overdue reminders
        logger.info("\n7️⃣ Test: findOverdueReminders()");
        List<Reminder> overdue = reminderRepository.findOverdueReminders(LocalDateTime.now());
        logger.info("   {} overdue reminders found", overdue.size());

        // Test 8: Count operations
        logger.info("\n8️⃣ Test: countUnsentByUserId() and countDueReminders()");
        Long userUnsentCount = reminderRepository.countUnsentByUserId(ryanId);
        Long totalDueCount = reminderRepository.countDueReminders(LocalDateTime.now());
        logger.info("   Ryan's unsent: {}, Total due: {}", userUnsentCount, totalDueCount);

        // Test 9: Check if app has unsent reminders
        logger.info("\n9️⃣ Test: hasUnsentReminders()");
        boolean hasUnsent = reminderRepository.hasUnsentReminders(app1Id);
        logger.info("   Application #{} has unsent reminders: {}", app1Id, hasUnsent);

        // Test 10: Mark reminder as sent (demonstrate @Modifying query)
        logger.info("\n🔟 Test: markAsSent()");
        if (!dueReminders.isEmpty()) {
            Long reminderId = dueReminders.get(0).getId();
            reminderRepository.markAsSent(reminderId);
            logger.info("   ✓ Marked reminder #{} as sent", reminderId);

            // Verify it was marked
            Optional<Reminder> updated = reminderRepository.findById(reminderId);
            updated.ifPresent(r -> logger.info("   Verified - getSent: {}", r.getSent()));
        }

        // Test 11: Find reminders in date range
        logger.info("\n1️⃣1️⃣ Test: findByReminderDateBetween()");
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        List<Reminder> inRange = reminderRepository.findByReminderDateBetween(start, end);
        logger.info("   Found {} reminders between now-1d and now+3d", inRange.size());

        // Test 12: Batch mark as sent
        logger.info("\n1️⃣2️⃣ Test: markMultipleAsSent()");
        List<Reminder> overdueList = reminderRepository.findOverdueReminders(LocalDateTime.now());
        if (!overdueList.isEmpty()) {
            List<Long> ids = overdueList.stream()
                    .filter(r -> !r.getSent())
                    .map(Reminder::getId)
                    .limit(2)
                    .toList();
            if (!ids.isEmpty()) {
                reminderRepository.markMultipleAsSent(ids);
                logger.info("   ✓ Marked {} reminders as sent in batch", ids.size());
            }
        }
    }
}