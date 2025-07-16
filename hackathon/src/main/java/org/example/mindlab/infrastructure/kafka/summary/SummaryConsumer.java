package org.example.mindlab.infrastructure.kafka.summary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryConsumer {


    @KafkaListener(topics = "summary_create", groupId = "summary_consumer")
    public void consume(SummaryCreateEvent event) {
        try {
            String postId = event.getPostId();
        } catch (Exception e) {
            log.error("요약 생성 실패");
        }
    }
}
