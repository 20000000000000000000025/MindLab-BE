package org.example.mindlab.application.usecase.dto.response;

import lombok.Builder;
import lombok.Data;

import org.example.mindlab.domain.subject.Subject;
import org.example.mindlab.domain.summation.Summation;
import org.springframework.data.domain.Page;

@Data
@Builder
public class QuerySummationDetailsResponse {

    private Long id;
    private String title;
    private String content;
    private String problem;
    private String feedback;

    private Page<SummationDetailsDto> subjects;
    private Long viewCount;

    public static QuerySummationDetailsResponse of(Summation summation, Page<Subject> subjectPage, Long viewCount) {
        Page<SummationDetailsDto> subjectResponses = subjectPage.map(
                s -> new SummationDetailsDto(s.getId(), s.getName())
        );

        return QuerySummationDetailsResponse.builder()
                .id(summation.getId())
                .title(summation.getTitle())
                .content(summation.getContent())
                .problem(summation.getProblem())
                .feedback(summation.getFeedback())
                .subjects(subjectResponses)
                .viewCount(viewCount)
                .build();
    }
}