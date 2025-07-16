package org.example.mindlab.application.usecase.dto.response;

import lombok.Builder;
import lombok.Data;

import org.example.mindlab.domain.summation.Summation;

import java.util.List;

@Data
@Builder
public class QuerySummationDetailsResponse {

    private Long id;

    private String title;

    private String content;

    private String problem;

    private String feedback;

    private List<String> subjects;

    private Long viewCount;

    private Long likeCount;

    public static QuerySummationDetailsResponse of(Summation summation, List<String> subjects,
                                                   Long viewCount, Long likeCount) {
        return QuerySummationDetailsResponse.builder()
                .id(summation.getId())
                .title(summation.getTitle())
                .content(summation.getContent())
                .problem(summation.getProblem())
                .feedback(summation.getFeedback())
                .subjects(subjects)
                .viewCount(viewCount)
                .likeCount(likeCount)
                .build();
    }
}