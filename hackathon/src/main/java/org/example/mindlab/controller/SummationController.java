package org.example.mindlab.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.mindlab.application.usecase.QueryAllSubjectsUseCase;
import org.example.mindlab.application.usecase.QuerySummationUseCase;
import org.example.mindlab.application.usecase.dto.response.QueryAllSubjectsResponse;
import org.example.mindlab.application.usecase.dto.response.QuerySummationDetailsResponse;
import org.example.mindlab.application.usecase.dto.response.QuerySummationsResponse;
import org.example.mindlab.domain.like.Like;
import org.example.mindlab.domain.like.QueryLikeRepository;
import org.example.mindlab.domain.like.repository.LikeRepository;
import org.example.mindlab.domain.summation.QuerySummationService;
import org.example.mindlab.domain.summation.Summation;
import org.example.mindlab.domain.summation.dto.response.QuerySummationResponse;
import org.example.mindlab.domain.summation.repository.QuerySummationRepository;
import org.example.mindlab.domain.summation.repository.SummationRepository;
import org.example.mindlab.global.authentication.AuthenticatedUserProvider;
import org.example.mindlab.global.exception.error.ErrorCodes;
import org.example.mindlab.infrastructure.cache.service.GetViewCountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/summations")
public class SummationController {

    private final QuerySummationUseCase querySummationUseCase;
    private final QuerySummationService querySummationService;

    private final QueryAllSubjectsUseCase queryAllSubjectsUseCase;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final QuerySummationRepository querySummationRepository;

    private final GetViewCountService getViewCountService;

    private final LikeRepository likeRepository;
    private final SummationRepository summationRepository;

    private final QueryLikeRepository queryLikeRepository;

    @GetMapping("/{summation-id}")
    public QuerySummationDetailsResponse querySummation(@PathVariable("summation-id") Long id) {
        return querySummationUseCase.execute(id);
    }

    @GetMapping
    public QuerySummationResponse queryAllSummation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false, name = "search-term") String searchTerm
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return querySummationService.execute(pageable, tags, searchTerm);
    }
  
    @GetMapping("/subjects")
    public QueryAllSubjectsResponse queryAllSummation() {
        return queryAllSubjectsUseCase.execute();
    }

    @GetMapping("/my")
    public QuerySummationResponse queryMySummation(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size,
        @RequestParam(defaultValue = "") String tags,
        @RequestParam(defaultValue = "", name = "search-term") String searchTerm
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long userId = authenticatedUserProvider.getCurrentUserId();

        // 페이지 조회
        Page<Summation> summationPage = querySummationRepository.queryMySummationsWithPaging(
            pageable, tags, searchTerm, userId
        );

        // ID 목록 추출
        List<Long> summationIds = summationPage.getContent().stream()
            .map(Summation::getId)
            .toList();

        // 조회수 및 좋아요 정보 가져오기
        Map<Long, Long> viewCounts = getViewCountService.getViewCounts(summationIds);
        List<Long> likes = queryLikeRepository.getLikeCountsByPostsId(summationIds); // 좋아요 서비스 호출

        return QuerySummationResponse.of(summationPage, viewCounts, likes);
    }

    @GetMapping("/liked")
    public QuerySummationResponse queryLikedSummation(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size,
        @RequestParam(defaultValue = "") String tags,
        @RequestParam(defaultValue = "", name = "search-term") String searchTerm
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long userId = authenticatedUserProvider.getCurrentUserId();

        // 페이지 조회
        Page<Summation> summationPage = querySummationRepository.queryLikedSummationsWithPaging(
            pageable, tags, searchTerm, userId
        );

        // ID 목록 추출
        List<Long> summationIds = summationPage.getContent().stream()
            .map(Summation::getId)
            .toList();

        // 조회수 및 좋아요 정보 가져오기
        Map<Long, Long> viewCounts = getViewCountService.getViewCounts(summationIds);
        List<Long> likes = queryLikeRepository.getLikeCountsByPostsId(summationIds); // 좋아요 서비스 호출

        return QuerySummationResponse.of(summationPage, viewCounts, likes);
    }


    @PostMapping("/like/{summation-id}")
    private void addLike(@PathVariable("summation-id") Long summationId) {
        Long userId = authenticatedUserProvider.getCurrentUserId();

        // summation 존재 확인
        Summation summation = summationRepository.findById(summationId).orElseThrow(ErrorCodes.SUMMATION_NOT_FOUND::throwException);

        // 중복 좋아요 체크
        likeRepository.findByUserIdAndSummation(userId, summation);

        likeRepository.save(Like.builder()
            .userId(userId)
            .summation(summation)
            .build());
    }

    @DeleteMapping("/like/{summation-id}")
    private void deleteLike(@PathVariable("summation-id") Long summationId) {
        Long userId = authenticatedUserProvider.getCurrentUserId();

        Summation summation = summationRepository.findById(summationId).orElseThrow(ErrorCodes.SUMMATION_NOT_FOUND::throwException);

        // 좋아요 존재 확인
        Long id = likeRepository.findByUserIdAndSummation(userId, summation).orElseThrow(ErrorCodes.SUMMATION_NOT_FOUND::throwException).getId();

        likeRepository.deleteById(id);
    }

}