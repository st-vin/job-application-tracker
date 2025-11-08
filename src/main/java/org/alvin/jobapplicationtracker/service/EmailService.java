package org.alvin.jobapplicationtracker.service;

public interface EmailService {
    void sendVerificationEmail(String to, String token, String firstName);
    void sendPasswordResetEmail(String to, String token, String firstName);
}

