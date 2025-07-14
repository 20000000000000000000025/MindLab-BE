package org.example.mindlab.domain.summation.repository;

import org.example.mindlab.domain.summation.Summation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummationRepository extends JpaRepository<Summation, Long> {
}
