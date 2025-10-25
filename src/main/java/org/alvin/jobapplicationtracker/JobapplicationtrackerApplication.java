package org.alvin.jobapplicationtracker;

import jakarta.transaction.Transactional;
import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.alvin.jobapplicationtracker.entity.Reminder;
import org.alvin.jobapplicationtracker.entity.UserEntity;
import org.alvin.jobapplicationtracker.repository.ApplicationRepository;
import org.alvin.jobapplicationtracker.repository.UserRepository;
import org.alvin.jobapplicationtracker.repository.ReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.alvin.jobapplicationtracker.entity.ApplicationStatus.OFFER;
import static org.alvin.jobapplicationtracker.entity.ApplicationStatus.TECHNICAL;

@SpringBootApplication
public class JobapplicationtrackerApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(JobapplicationtrackerApplication.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    public static void main(String[] args) {
        SpringApplication.run(JobapplicationtrackerApplication.class, args);
    }

    /**
     * Transactional startup data initializer.
     *
     * Behavior:
     *  - If the DB is empty (no users, no applications, no reminders) -> seed sample data.
     *  - If the DB already has data -> skip reseeding.
     *
     * Rationale:
     *  - Typical for dev/test environments: seed only once on a fresh database.
     */
    @Transactional
    @Override
    public void run(String... args) throws Exception {

        // Check DB state
        boolean hasUsers = userRepository.count() > 0;
        boolean hasApplications = applicationRepository.count() > 0;
        boolean hasReminders = reminderRepository.count() > 0;

        // If empty -> seed sample data
        if (!hasUsers && !hasApplications && !hasReminders) {
            logger.info("📦 Database empty. Seeding sample data...");

            // -----------------------------
            // Create sample users
            // -----------------------------
            UserEntity ryan = new UserEntity("Ryan", "Coogler", "ryancoogler@gmail.com", "nicepasswordryan");
            UserEntity ford = new UserEntity("Ford", "Coppola", "fordcoppola@email.com", "TheGodfather");
            UserEntity wim = new UserEntity("Wim", "Wenders", "wenderwim@hotmail.com", "perfectDays");

            userRepository.saveAll(List.of(ryan, ford, wim));

            logger.info("✅ Users inserted:");
            userRepository.findAll().forEach(user -> logger.info("User: {}", user));

            // -----------------------------
            // Create sample applications
            // -----------------------------
            ApplicationEntity app1 = new ApplicationEntity(
                    "Google", "Junior Engineer", OFFER, LocalDate.of(2024, 1, 15), 80000.0,
                    "LinkedIn", "https://google.com/jobs", "Great company!",
                    LocalDateTime.now().minusDays(30), LocalDateTime.now(), ryan
            );

            ApplicationEntity app2 = new ApplicationEntity(
                    "Amazon", "Software Developer", TECHNICAL, LocalDate.of(2024, 2, 20), 100000.0,
                    "Indeed", "https://amazon.com/jobs", "Excellent opportunity to learn and grow.",
                    LocalDateTime.now().minusDays(15), LocalDateTime.now().plusDays(10), ford
            );

            ApplicationEntity app3 = new ApplicationEntity(
                    "Microsoft", "Data Scientist", OFFER, LocalDate.of(2024, 3, 1), 120000.0,
                    "Glassdoor", "https://microsoft.com/jobs", "Innovative projects.",
                    LocalDateTime.now().minusDays(20), LocalDateTime.now().plusDays(5), wim
            );

            ApplicationEntity app4 = new ApplicationEntity(
                    "Safaricom", "Senior Security Analyst", OFFER, LocalDate.of(2025, 3, 1), 220000.0,
                    "BrightMonday", "https://safaricom.com/jobs", "Intensive analysis.",
                    LocalDateTime.now().minusDays(20), LocalDateTime.now().plusDays(5), ryan
            );

            applicationRepository.saveAll(List.of(app1, app2, app3, app4));

            logger.info("✅ Applications inserted:");
            applicationRepository.findAll().forEach(app ->
                    logger.info("Application: {} - {} for user {}", app.getCompanyName(), app.getPosition(), app.getUser().getFirstName())
            );

            // -----------------------------
            // Create sample reminders (FIXED: removed explicit ID assignment)
            // -----------------------------
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

            reminderRepository.saveAll(List.of(reminder1, reminder2));

            logger.info("\n⏰ Unsent reminders that are due:");
            /*reminderRepository.findByIsSentFalseAndReminderDateBefore(LocalDateTime.now())
                    .forEach(r -> logger.info("Reminder due: {} for {}", r.getReminderMessage(), r.getApplication().getCompanyName()));*/

            logger.info("✅ Database seeded successfully.");
        } else {
            logger.info("ℹ️ Database already contains data (users={}, apps={}, reminders={}). Skipping seeding.",
                    hasUsers, hasApplications, hasReminders);
        }
    }
}