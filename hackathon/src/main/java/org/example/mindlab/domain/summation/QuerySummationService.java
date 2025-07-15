package org.example.mindlab.domain.summation;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.domain.summation.repository.QuerySummationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuerySummationService {

    private final QuerySummationRepository querySummationRepository;

    public Page<Summation> execute(Pageable pageable) {
        return querySummationRepository.querySummationsWithPaging(pageable);
    }
}
