package org.example.mindlab.controller;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.application.usecase.QueryAllSubjectsUseCase;
import org.example.mindlab.application.usecase.QuerySummationUseCase;
import org.example.mindlab.application.usecase.dto.response.QueryAllSubjectsResponse;
import org.example.mindlab.application.usecase.dto.response.QuerySummationDetailsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/summations")
public class SummationController {

    private final QuerySummationUseCase querySummationUseCase;

    private final QueryAllSubjectsUseCase queryAllSubjectsUseCase;

    @GetMapping("/{summation-id}")
    public QuerySummationDetailsResponse querySummation(@PathVariable("summation-id") Long id) {
        return querySummationUseCase.execute(id);
    }

    @GetMapping("/subjects")
    public QueryAllSubjectsResponse queryAllSubjects() {
        return queryAllSubjectsUseCase.execute();
    }
}