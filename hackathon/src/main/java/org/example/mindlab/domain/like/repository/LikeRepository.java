package org.example.mindlab.domain.like.repository;

import java.util.Optional;
import org.example.mindlab.domain.like.Like;
import org.example.mindlab.domain.summation.Summation;
import org.springframework.data.repository.CrudRepository;

public interface LikeRepository extends CrudRepository<Like, Long> {

    public Optional<Like> findByUserIdAndSummation(Long userId, Summation summation);
}
