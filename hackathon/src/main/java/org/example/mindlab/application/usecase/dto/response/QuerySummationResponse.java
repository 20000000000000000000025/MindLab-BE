package org.example.mindlab.application.usecase.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import org.example.mindlab.domain.summation.Summation;

@Data
@Builder
public class QuerySummationResponse {

    private Long id;

    private String title;

    private String content;

    private String problem;

    private String feedback;

    private List<String> subjects;

    private Long viewCount;

    public static QuerySummationResponse of(Summation summation, List<String> subjects, Long viewCount) {
        return QuerySummationResponse.builder()
            .id(summation.getId())
            .title(summation.getTitle())
            .content(summation.getContent())
            .problem(summation.getProblem())
            .feedback(summation.getFeedback())
            .subjects(subjects)
            .viewCount(viewCount)
            .build();
    }
}
