package org.alvin.jobapplicationtracker.controller;

import org.alvin.jobapplicationtracker.entity.ApplicationEntity;
import org.alvin.jobapplicationtracker.repository.ApplicationRepository;
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


    @GetMapping("/applications/{id}")
    public Optional<ApplicationEntity> getApplicationByIdWithUser(@PathVariable("id") Long id) {
        return applicationRepository.findByIdWithUser(id);
    }
}
