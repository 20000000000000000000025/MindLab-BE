package org.example.mindlab.infrastructure.kafka.event.viewcount;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.infrastructure.kafka.dto.KafkaEvent;
import org.example.mindlab.infrastructure.kafka.util.JsonSerializer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static org.example.mindlab.infrastructure.kafka.properties.KafkaTopicProperties.INCREASE_VIEW_TOPIC;

@RequiredArgsConstructor
@Component
public class IncreasePostViewProducer {

    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    private final JsonSerializer jsonSerializer;

    public void publish(IncreasePostViewEvent event) {
        KafkaEvent kafkaEvent = KafkaEvent.builder()
            .topic(INCREASE_VIEW_TOPIC)
            .eventClass(IncreasePostViewEvent.class)
            .payload(jsonSerializer.toJson(event))
            .retryCount(0)
            .build();

        kafkaTemplate.send(INCREASE_VIEW_TOPIC, kafkaEvent);
    }
}
