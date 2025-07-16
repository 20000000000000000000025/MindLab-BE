package org.example.mindlab.infrastructure.kafka.summary;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SummaryProducer {

    private final KafkaTemplate<String, SummaryCreateEvent> kafkaTemplate;

    public void send(String postId) {
        SummaryCreateEvent event = SummaryCreateEvent.builder()
                .postId(postId)
                .build();
        kafkaTemplate.send("summary_create", event);
    }
}