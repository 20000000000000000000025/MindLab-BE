package org.example.mindlab.infrastructure.kafka.summary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mindlab.infrastructure.client.rest.RestClient;
import org.example.mindlab.infrastructure.client.rest.dto.RestClientResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryConsumer {
    private final RestClient restClient;

    @KafkaListener(topics = "summary_create", groupId = "summary_consumer")
    public void consume(SummaryCreateEvent event) {
        try {
            RestClientResponse response = restClient.sendImageToAi(event.getPostId());
        } catch (Exception e) {
            log.error("요약 생성 실패");
        }
    }
}
