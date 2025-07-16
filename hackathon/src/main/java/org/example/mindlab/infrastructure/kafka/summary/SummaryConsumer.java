package org.example.mindlab.infrastructure.kafka.summary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mindlab.domain.summation.Summation;
import org.example.mindlab.domain.summation.repository.SummationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryConsumer {

    private final SummationRepository summationRepository;


    @KafkaListener(topics = "summary_complete", groupId = "summary_consumer")
    public void consume(SummaryCompletedEvent event) {
        try {
            Summation summation = new Summation();
            summation.setTitle(event.getTitle());
            summation.setContent(event.getSummary());
            summation.setProblem(event.getQuestion());

            summationRepository.save(summation);

        } catch (Exception e) {
            log.error("요약 생성 실패");
        }
    }
}
