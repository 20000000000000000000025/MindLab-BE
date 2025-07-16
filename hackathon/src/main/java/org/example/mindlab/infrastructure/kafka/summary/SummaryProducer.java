package org.example.mindlab.infrastructure.kafka.summary;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SummaryProducer {

    private final KafkaTemplate<String, SummaryCreateEvent> kafkaTemplate;

    public void send(Long summationId) {
        SummaryCreateEvent event = SummaryCreateEvent.builder()
                .summaryId(summationId)
                .build();
        kafkaTemplate.send("summary_create", event);
    }
}