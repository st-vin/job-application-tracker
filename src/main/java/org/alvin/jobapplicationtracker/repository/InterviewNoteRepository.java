package org.alvin.jobapplicationtracker.repository;

import org.alvin.jobapplicationtracker.entity.InterviewNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewNoteRepository extends JpaRepository<InterviewNote, Long> {
}
