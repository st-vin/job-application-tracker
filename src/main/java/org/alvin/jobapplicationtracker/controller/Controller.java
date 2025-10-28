package org.alvin.jobapplicationtracker.controller;

import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.alvin.jobapplicationtracker.entity.UserEntity;
import org.alvin.jobapplicationtracker.repository.ApplicationRepository;
import org.alvin.jobapplicationtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class Controller {
    @Autowired
    public ApplicationRepository applicationRepository;
    
    @Autowired
    public UserRepository userRepository;

    @GetMapping("/applications/{id}")
    public Optional<ApplicationEntity> getApplicationByIdWithUser(@PathVariable("id") Long id) {
        return applicationRepository.findByIdWithUser(id);
    }
    
    @GetMapping("/users/{id}")
    public Optional<UserEntity> getUserByIdWithApplications(@PathVariable("id") Long id) {
        return userRepository.findByIdWithApplications(id);
    }
    
    @GetMapping("/applications")
    public List<ApplicationEntity> getAllApplications() {
        return applicationRepository.findAll();
    }
    
    @GetMapping("/users")
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}
