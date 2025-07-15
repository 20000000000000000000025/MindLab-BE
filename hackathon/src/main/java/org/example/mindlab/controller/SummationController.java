package org.example.mindlab.controller;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.application.usecase.QuerySummationUseCase;
import org.example.mindlab.application.usecase.dto.response.QuerySummationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/summations")
public class SummationController {

    private final QuerySummationUseCase querySummationUseCase;

    @GetMapping("/{summation-id}")
    public QuerySummationResponse querySummation(@PathVariable("summation-id") Long id) {
        return querySummationUseCase.execute(id);
    }
}
