package org.example.mindlab.application.usecase.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueryAllSubjectsResponse {

    private List<String> subjects;
}
