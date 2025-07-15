package org.example.mindlab.application.usecase;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.application.usecase.dto.response.QuerySummationDetailsResponse;
import org.example.mindlab.domain.subject.QuerySubjectRepository;
import org.example.mindlab.domain.subject.Subject;
import org.example.mindlab.domain.summation.Summation;
import org.example.mindlab.domain.summation.repository.SummationRepository;
import org.example.mindlab.global.authentication.AuthenticatedUserProvider;
import org.example.mindlab.infrastructure.cache.service.GetViewCountService;
import org.example.mindlab.infrastructure.kafka.event.viewcount.IncreasePostViewEvent;
import org.example.mindlab.infrastructure.kafka.event.viewcount.IncreasePostViewProducer;
import org.springframework.stereotype.Component;
import java.util.List;

import static org.example.mindlab.global.exception.error.ErrorCodes.SUMMATION_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class QuerySummationUseCase {

    private final SummationRepository summationRepository;

    private final GetViewCountService getViewCountService;

    private final QuerySubjectRepository querySubjectRepository;

    private final IncreasePostViewProducer increasePostViewProducer;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public QuerySummationDetailsResponse execute(Long id) {
        Summation summation = summationRepository.findById(id)
            .orElseThrow(SUMMATION_NOT_FOUND::throwException);

        List<String> subjects = querySubjectRepository.querySubjectBySummationId(id)
            .stream().map(Subject :: getName).toList();

        Long userId = authenticatedUserProvider.getCurrentUserId();

        increasePostViewProducer.publish(IncreasePostViewEvent.builder()
            .postId(id)
            .userId(userId)
            .build());

        Long viewCount = getViewCountService.getViewCounts(List.of(id)).get(id);

        return QuerySummationDetailsResponse.of(summation, subjects, viewCount);
    }
}
