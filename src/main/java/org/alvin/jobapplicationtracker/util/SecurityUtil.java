// util/SecurityUtil.java
package org.alvin.jobapplicationtracker.util;

import org.alvin.jobapplicationtracker.entity.UserEntity;
import org.alvin.jobapplicationtracker.exception.ResourceNotFoundException;
import org.alvin.jobapplicationtracker.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final UserRepository userRepository;

    public SecurityUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        String email = authentication.getName();
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}