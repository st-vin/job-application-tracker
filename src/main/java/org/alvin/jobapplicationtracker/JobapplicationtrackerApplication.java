package org.alvin.jobapplicationtracker;

import jakarta.transaction.Transactional;
import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
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

import java.time.LocalDateTime;

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

        // --- Log saved users ---
        userRepository.findAll().forEach(user -> logger.info("User saved: {}", user));

        // --- Create sample applications (linked to users) ---
        ApplicationEntity app1 = new ApplicationEntity(
                "Google", "Junior Engineer", OFFER, "2024-01-15", 80000.0,
                "LinkedIn", "https://google.com/jobs", "Great company!",
                LocalDateTime.now().minusDays(30), LocalDateTime.now(), ryan
        );

        ApplicationEntity app2 = new ApplicationEntity(
                "Amazon", "Software Developer", TECHNICAL, "2024-02-20", 100000.0,
                "Indeed", "https://amazon.com/jobs", "Excellent opportunity to learn and grow.",
                LocalDateTime.now().minusDays(15), LocalDateTime.now().plusDays(10), ford
        );

        ApplicationEntity app3 = new ApplicationEntity(
                "Microsoft", "Data Scientist", OFFER, "2024-03-01", 120000.0,
                "Glassdoor", "https://microsoft.com/jobs", "Innovative projects.",
                LocalDateTime.now().minusDays(20), LocalDateTime.now().plusDays(5), wim
        );

        ApplicationEntity app4 = new ApplicationEntity(
                "Safaricom", "Senior Security Analyst", OFFER, "2025-03-01", 220000.0,
                "BrightMonday", "https://safaricom.com/jobs", "Intensive analysis.",
                LocalDateTime.now().minusDays(20), LocalDateTime.now().plusDays(5), ryan
        );

        // --- Save applications ---
        applicationRepository.save(app1);
        applicationRepository.save(app2);
        applicationRepository.save(app3);
        applicationRepository.save(app4);

        // --- Log saved applications ---
        applicationRepository.findAll().forEach(app ->
                logger.info("Application saved: {} - {} for user {}", app.getCompanyName(), app.getPosition(), app.getUser().getFirstName())
        );

        logger.info("✅ Sample data inserted successfully!");
    }
}
