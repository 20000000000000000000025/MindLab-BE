package org.example.mindlab.domain.summation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.mindlab.domain.summation.Summation;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class QuerySummationResponse {

    private List<SummationListDto> summations;

    public static QuerySummationResponse of(Page<Summation> page, Map<Long, Long> viewCounts, List<Long> likes) {
        List<Summation> summations = page.getContent();

        List<SummationListDto> summationListDtos = summations.stream()
            .map(s -> {
                int index = summations.indexOf(s);
                Long likeCount = (index < likes.size()) ? likes.get(index) : 0L;

                return SummationListDto.builder()
                    .id(s.getId())
                    .title(s.getTitle())
                    .content(s.getContent())
                    .authorName(s.getUserId() != null ? s.getUserId().toString() : "Unknown")
                    .viewCount(viewCounts.getOrDefault(s.getId(), 0L))
                    .likeCount(likeCount)
                    .build();
            })
            .collect(Collectors.toList());

        return new QuerySummationResponse(summationListDtos);
    }

    @Builder
    public record SummationListDto(
        Long id,
        String title,
        String content,
        String authorName,
        Long viewCount,
        Long likeCount
    ) {
    }
}