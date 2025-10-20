package org.alvin.jobapplicationtracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.alvin.jobapplicationtracker.entity.Reminder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
/*
    // find unsent reminders
    List<Reminder> findByIsSentFalseAndReminderDateBefore(LocalDateTime now);

    // find reminders by id
    List<Reminder> findByApplicationEntity_id(Long id);
*/

}
