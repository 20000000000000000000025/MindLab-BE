package org.example.mindlab.domain.summation;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.domain.like.QueryLikeRepository;
import org.example.mindlab.domain.summation.dto.response.QuerySummationResponse;
import org.example.mindlab.domain.summation.repository.QuerySummationRepository;
import org.example.mindlab.infrastructure.cache.service.GetViewCountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class QuerySummationService {

    private final QuerySummationRepository querySummationRepository;
    private final GetViewCountService getViewCountService;

    private final QueryLikeRepository queryLikeRepository;

    public QuerySummationResponse execute(Pageable pageable, String tags, String searchTerm) {

        Page<Summation> page = querySummationRepository
            .querySummationsWithPaging(pageable, tags, searchTerm);

        List<Long> ids = page.getContent().stream()
                .map(Summation::getId)
                .toList();

        Map<Long, Long> viewCounts = getViewCountService.getViewCounts(ids);

        List<Long> likes = queryLikeRepository.getLikeCountsByPostsId(ids);

        return QuerySummationResponse.of(page, viewCounts, likes);

    }
}
