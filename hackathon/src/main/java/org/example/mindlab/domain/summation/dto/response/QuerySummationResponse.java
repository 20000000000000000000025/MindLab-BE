package org.example.mindlab.domain.summation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.mindlab.domain.summation.Summation;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class QuerySummationResponse {

    private List<SummationListDto> summations;

    public static QuerySummationResponse of(Page<Summation> page, Map<Long, Long> viewCounts) {
        List<SummationListDto> summationListDtos = page.getContent().stream()
                .map(s -> SummationListDto.builder()
                        .id(s.getId())
                        .title(s.getTitle())
                        .viewCount(viewCounts.getOrDefault(s.getId(), 0L))
                        .build())
                .collect(Collectors.toList());

        return new QuerySummationResponse(summationListDtos);
    }

    @Builder
    public record SummationListDto(
            Long id,
            String title,
            Long viewCount
    ) {
    }
}
