package org.example.mindlab.application.usecase;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.domain.summation.Summation;
import org.example.mindlab.domain.summation.repository.SummationRepository;
import org.example.mindlab.global.authentication.AuthenticatedUserProvider;
import org.example.mindlab.infrastructure.client.rest.AIRestClient;
import org.example.mindlab.infrastructure.kafka.summary.SummaryCreateEvent;
import org.example.mindlab.infrastructure.kafka.summary.SummaryProducer;
import org.example.mindlab.infrastructure.s3.service.S3Service;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class CreateSummationUseCase {

    private final SummaryProducer summaryProducer;

    private final S3Service s3Service;

    private final SummationRepository summationRepository;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public void execute(MultipartFile multipartFile) {

        Long userId = authenticatedUserProvider.getCurrentUserId();

        Summation summation = summationRepository.save(Summation
            .builder()
            .userId(userId)
            .build());

        s3Service.upload(multipartFile, summation.getId());

        summaryProducer.send(summation.getId());
    }
}
