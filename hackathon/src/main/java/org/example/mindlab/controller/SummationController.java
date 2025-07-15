package org.example.mindlab.controller;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.application.usecase.QuerySummationUseCase;
import org.example.mindlab.application.usecase.dto.response.QuerySummationDetailsResponse;
import org.example.mindlab.domain.summation.QuerySummationService;
import org.example.mindlab.domain.summation.dto.response.QuerySummationResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/summations")
public class SummationController {

    private final QuerySummationUseCase querySummationUseCase;
    private final QuerySummationService querySummationService;

    @GetMapping("/{summation-id}")
    public QuerySummationDetailsResponse querySummation(@PathVariable("summation-id") Long id) {
        return querySummationUseCase.execute(id);
    }

    @GetMapping
    public QuerySummationResponse queryAllSummation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return querySummationService.execute(pageable);
    }
}