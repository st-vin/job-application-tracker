// service/AuthService.java
package org.alvin.jobapplicationtracker.service;

import org.alvin.jobapplicationtracker.dto.request.LoginRequest;
import org.alvin.jobapplicationtracker.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
}