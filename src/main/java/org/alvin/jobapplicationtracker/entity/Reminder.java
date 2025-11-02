package org.alvin.jobapplicationtracker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminder")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    @JsonBackReference("application-reminder") // ✅ Prevents recursion
    private ApplicationEntity application;

    @Column(nullable = false)
    private LocalDateTime reminderDate;

    @Column(nullable = false)
    private String reminderMessage;

    @Column(nullable = false)
    private boolean isSent = false;

    public Reminder() {
    }

    public Reminder(LocalDateTime reminderDate, String reminderMessage, boolean isSent) {
        this.reminderDate = reminderDate;
        this.reminderMessage = reminderMessage;
        this.isSent = isSent;
    }

    public Reminder(ApplicationEntity application, LocalDateTime reminderDate, String reminderMessage, boolean isSent) {
        this.application = application;
        this.reminderDate = reminderDate;
        this.reminderMessage = reminderMessage;
        this.isSent = isSent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDateTime reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderMessage() {
        return reminderMessage;
    }

    public void setReminderMessage(String reminderMessage) {
        this.reminderMessage = reminderMessage;
    }

    public boolean getSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public ApplicationEntity getApplication() {
        return application;
    }

    public void setApplication(ApplicationEntity application) {
        this.application = application;
    }

}
