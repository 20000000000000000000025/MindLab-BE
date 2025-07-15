package org.example.mindlab.application.usecase;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.application.usecase.dto.response.QueryAllSubjectsResponse;
import org.example.mindlab.domain.subject.QuerySubjectRepository;
import org.example.mindlab.domain.subject.Subject;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class QueryAllSubjectsUseCase {

    private final QuerySubjectRepository querySubjectRepository;

    public QueryAllSubjectsResponse execute() {
        List<String> subjects = querySubjectRepository.getAllSubjects()
            .stream().map(Subject::getName).toList();
        return new QueryAllSubjectsResponse(subjects);
    }
}
