package org.alvin.jobapplicationtracker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "application")
@JsonIgnoreProperties({"hibernateLazyInitializer","Handler"})
public class ApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private LocalDate appliedDate;
    private double salary;
    private String jobBoardSource;

    @Column(length = 1000)
    private String jobUrl;

    @Column(length = 5000)
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // many-to-one relationship with user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // foreign key column name
    @JsonBackReference("user-applications") //  Prevent infinite loop
    private UserEntity user;

    // one-to-many relationship with status_history
    @JsonManagedReference("application-status_history")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "application")
    private List<StatusHistory> statusHistory = new ArrayList<>();

    // one-to-many relationship with Reminder
    @JsonManagedReference("application-reminder")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "application")
    private List<Reminder> reminders = new ArrayList<>();

    // one-to-many relationship with interview_note
    @JsonManagedReference("application-interview_note")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "application")
    private List<InterviewNote> interviewNotes = new ArrayList<>();

    // no argument constructor
    public ApplicationEntity() {
    }

    // constructor
    public ApplicationEntity(
            String companyName,
            String position,
            ApplicationStatus status,
            LocalDate appliedDate,
            double salary,
            String jobBoardSource,
            String jobUrl,
            String notes,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            UserEntity user
    ) {
        this.companyName = companyName;
        this.position = position;
        this.status = status;
        this.appliedDate = LocalDate.from(createdAt);
        this.salary = salary;
        this.jobBoardSource = jobBoardSource;
        this.jobUrl = jobUrl;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getJobBoardSource() {
        return jobBoardSource;
    }

    public void setJobBoardSource(String jobBoardSource) {
        this.jobBoardSource = jobBoardSource;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<StatusHistory> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<StatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    public List<InterviewNote> getInterviewNotes() {
        return interviewNotes;
    }
    public void setInterviewNotes(List<InterviewNote> interviewNotes) {
        this.interviewNotes = interviewNotes;
    }
}
