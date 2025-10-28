package org.alvin.jobapplicationtracker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name = "interview_note")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InterviewNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    @JsonBackReference("application-interview_note")
    private ApplicationEntity application;

    @Column(length = 5000)
    private String note;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String interviewStage;

    public InterviewNote() {
    }

    public InterviewNote(String note, LocalDateTime createdAt, String interviewStage, ApplicationEntity application) {
        this.note = note;
        this.createdAt = createdAt;
        this.interviewStage = interviewStage;
        this.application = application;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getInterviewStage() {
        return interviewStage;
    }

    public void setInterviewStage(String interviewStage) {
        this.interviewStage = interviewStage;
    }

    public ApplicationEntity getApplication() {
        return application;
    }

    public void setApplication(ApplicationEntity application) {
        this.application = application;
    }

    @Override
    public String toString() {
        return "InterviewNote{" +
                "id=" + id +
                ", application=" + application +
                ", note='" + note + '\'' +
                ", createdAt=" + createdAt +
                ", interviewStage='" + interviewStage + '\'' +
                '}';
    }
}
