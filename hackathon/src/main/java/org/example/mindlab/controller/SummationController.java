package org.example.mindlab.controller;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.application.usecase.QuerySummationUseCase;
import org.example.mindlab.application.usecase.dto.response.QuerySummationDetailsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/summations")
public class SummationController {

    private final QuerySummationUseCase querySummationUseCase;

    @GetMapping("/{summation-id}")
    public QuerySummationDetailsResponse getSummationDetails(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return querySummationUseCase.execute(id, pageable);
    }
}