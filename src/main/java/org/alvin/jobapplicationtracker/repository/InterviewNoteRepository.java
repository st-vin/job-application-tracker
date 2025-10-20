package org.alvin.jobapplicationtracker.repository;

import org.springframework.data.repository.CrudRepository;
import org.alvin.jobapplicationtracker.entity.InterviewNote;

public interface InterviewNoteRepository extends CrudRepository<InterviewNote, Long> {
}
