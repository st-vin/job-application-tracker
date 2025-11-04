// security/CustomUserDetailsService.java
package org.alvin.jobapplicationtracker.security;

import lombok.RequiredArgsConstructor;
import org.alvin.jobapplicationtracker.entity.UserEntity;
import org.alvin.jobapplicationtracker.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));

        // Convert role to authority (Spring Security expects ROLE_ prefix)
        List<GrantedAuthority> authorities = user.getRole() != null
                ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                : Collections.emptyList();

        // Convert your UserEntity to Spring Security's UserDetails
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}