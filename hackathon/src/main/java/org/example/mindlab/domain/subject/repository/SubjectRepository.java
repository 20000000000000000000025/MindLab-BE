package org.example.mindlab.domain.subject.repository;

import org.example.mindlab.domain.subject.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
