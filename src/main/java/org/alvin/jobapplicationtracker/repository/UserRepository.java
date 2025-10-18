package org.alvin.jobapplicationtracker.repository;

import org.alvin.jobapplicationtracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long>{

}