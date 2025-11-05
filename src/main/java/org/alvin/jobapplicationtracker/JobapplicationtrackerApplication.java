package org.alvin.jobapplicationtracker;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.alvin.jobapplicationtracker.entity.*;
import org.alvin.jobapplicationtracker.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.alvin.jobapplicationtracker.entity.ApplicationStatus.*;
import static org.alvin.jobapplicationtracker.entity.Role.ADMIN;
import static org.alvin.jobapplicationtracker.entity.Role.USER;

@SpringBootApplication
public class JobapplicationtrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobapplicationtrackerApplication.class, args);
    }

    @Component
    @RequiredArgsConstructor
    static class DataSeeder implements CommandLineRunner {

        private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

        private final UserRepository userRepository;
        private final ApplicationRepository applicationRepository;
        private final ReminderRepository reminderRepository;
        private final StatusHistoryRepository statusHistoryRepository;
        private final InterviewNoteRepository interviewNoteRepository;

        @Override
        @Transactional
        public void run(String... args) {
            try {
                seedUsers();
                seedApplications();
                seedReminders();
                seedStatusHistory();
                seedInterviewNotes();

                logger.info("🎉 All database seeding completed successfully!");
            } catch (Exception e) {
                logger.error("⚠️ Seeding failed, but application continues running.", e);
            }
        }

        private void seedUsers() {
            if (userRepository.count() > 0) {
                logger.info("Users already exist, skipping seeding.");
                return;
            }

            UserEntity ryan = new UserEntity("Ryan", "Coogler", "ryancoogler@gmail.com", "nicepasswordryan", ADMIN);
            UserEntity ford = new UserEntity("Ford", "Coppola", "fordcoppola@email.com", "TheGodfather", USER);
            UserEntity wim = new UserEntity("Wim", "Wenders", "wenderwim@hotmail.com", "perfectDays", USER);

            userRepository.saveAll(List.of(ryan, ford, wim));
            logger.info("✅ Created {} users", userRepository.count());
        }

        private void seedApplications() {
            if (applicationRepository.count() > 0) {
                logger.info("Applications already exist, skipping seeding.");
                return;
            }

            List<UserEntity> users = userRepository.findAll();
            if (users.size() < 3) {
                logger.warn("Not enough users to seed applications, skipping.");
                return;
            }

            ApplicationEntity app1 = new ApplicationEntity(
                    "Google", "Junior Engineer", OFFER, LocalDate.of(2024, 1, 15), 80000.0,
                    "LinkedIn", "https://google.com/jobs", "Great company!",
                    LocalDateTime.now().minusDays(30), LocalDateTime.now(), users.get(0)
            );

            ApplicationEntity app2 = new ApplicationEntity(
                    "Amazon", "Software Developer", TECHNICAL, LocalDate.of(2024, 1, 15), 100000.0,
                    "Indeed", "https://amazon.com/jobs", "Excellent opportunity to learn and grow.",
                    LocalDateTime.now().minusDays(15), LocalDateTime.now().plusDays(10), users.get(1)
            );

            ApplicationEntity app3 = new ApplicationEntity(
                    "Microsoft", "Data Scientist", OFFER, LocalDate.of(2024, 1, 15), 120000.0,
                    "Glassdoor", "https://microsoft.com/jobs", "Innovative projects.",
                    LocalDateTime.now().minusDays(20), LocalDateTime.now().plusDays(5), users.get(2)
            );

            ApplicationEntity app4 = new ApplicationEntity(
                    "Safaricom", "Senior Security Analyst", APPLIED, LocalDate.of(2024, 1, 15), 220000.0,
                    "BrightMonday", "https://safaricom.com/jobs", "Intensive analysis.",
                    LocalDateTime.now().minusDays(20), LocalDateTime.now().plusDays(5), users.get(0)
            );

            ApplicationEntity app5 = new ApplicationEntity(
                    "Google", "Senior Backend Engineer", TECHNICAL, LocalDate.of(2024, 1, 15), 150000.0,
                    "LinkedIn", "https://google.com/careers", "Amazing benefits!",
                    LocalDateTime.now().minusDays(5), LocalDateTime.now(), users.get(1)
            );

            applicationRepository.saveAll(List.of(app1, app2, app3, app4, app5));
            logger.info("✅ Created {} applications", applicationRepository.count());
        }

        private void seedReminders() {
            if (reminderRepository.count() > 0) {
                logger.info("Reminders already exist, skipping seeding.");
                return;
            }

            List<ApplicationEntity> apps = applicationRepository.findAll();
            if (apps.isEmpty()) return;

            Reminder reminder1 = new Reminder(apps.get(0), LocalDateTime.now().plusDays(2), "Follow up with Google recruiter", false);
            Reminder reminder2 = new Reminder(apps.get(1), LocalDateTime.now().minusDays(1), "Prepare for Amazon technical test", false);
            Reminder reminder3 = new Reminder(apps.get(2), LocalDateTime.now().minusDays(4), "Send thank you email to Microsoft", false);
            Reminder reminder4 = new Reminder(apps.get(3), LocalDateTime.now().minusDays(3), "Check Safaricom application status", false);
            Reminder reminder5 = new Reminder(apps.get(4), LocalDateTime.now().minusDays(5), "Google interview prep", false);

            reminderRepository.saveAll(List.of(reminder1, reminder2, reminder3, reminder4, reminder5));
            logger.info("✅ Created {} reminders", reminderRepository.count());
        }

        private void seedStatusHistory() {
            if (statusHistoryRepository.count() > 0) {
                logger.info("Status history already exists, skipping seeding.");
                return;
            }

            List<ApplicationEntity> apps = applicationRepository.findAll();
            if (apps.isEmpty()) return;

            StatusHistory history1 = new StatusHistory(apps.get(0), null, OFFER, LocalDateTime.now().minusDays(25));
            StatusHistory history2 = new StatusHistory(apps.get(1), APPLIED, TECHNICAL, LocalDateTime.now().minusDays(10));
            StatusHistory history3 = new StatusHistory(apps.get(2), TECHNICAL, OFFER, LocalDateTime.now().minusDays(15));
            StatusHistory history4 = new StatusHistory(apps.get(3), null, APPLIED, LocalDateTime.now().minusDays(20));

            statusHistoryRepository.saveAll(List.of(history1, history2, history3, history4));
            logger.info("✅ Created {} status history entries", statusHistoryRepository.count());
        }

        private void seedInterviewNotes() {
            if (interviewNoteRepository.count() > 0) {
                logger.info("Interview notes already exist, skipping seeding.");
                return;
            }

            List<ApplicationEntity> apps = applicationRepository.findAll();
            if (apps.isEmpty()) return;

            InterviewNote note1 = new InterviewNote("Great conversation, strong technical background.", LocalDateTime.now().minusDays(28), "Phone Screen", apps.get(0));
            InterviewNote note2 = new InterviewNote("Scheduled for coding challenge.", LocalDateTime.now().plusDays(3), "Technical Interview", apps.get(1));
            InterviewNote note3 = new InterviewNote("Excellent communication skills.", LocalDateTime.now().minusDays(18), "Behavioral Interview", apps.get(2));
            InterviewNote note4 = new InterviewNote("Prepare scalability questions.", LocalDateTime.now().plusDays(5), "System Design", apps.get(4));

            interviewNoteRepository.saveAll(List.of(note1, note2, note3, note4));
            logger.info("✅ Created {} interview notes", interviewNoteRepository.count());
        }
    }
}