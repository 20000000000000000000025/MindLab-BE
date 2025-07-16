package org.example.mindlab.application.usecase.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.mindlab.domain.summation.Summation;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class QuerySummationsResponse {

    private List<SummationDetailsDto> summations;

    public static QuerySummationsResponse of(Page<Summation> page) {
        List<SummationDetailsDto> dtos = page.getContent().stream()
            .map(s -> SummationDetailsDto.builder()
                .id(s.getId())
                .title(s.getTitle())
                .build())
            .collect(Collectors.toList());

        return new QuerySummationsResponse(dtos);
    }

    @Builder
    public record SummationDetailsDto(
        Long id,
        String title,
        Long viewCount
    ) {
    }
}
