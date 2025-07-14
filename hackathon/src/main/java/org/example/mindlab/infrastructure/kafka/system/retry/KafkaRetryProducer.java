package org.example.mindlab.infrastructure.kafka.system.retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mindlab.infrastructure.kafka.dto.KafkaEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static org.example.mindlab.infrastructure.kafka.properties.KafkaTopicProperties.RETRY_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaRetryProducer {

    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    public void retryPublish(KafkaEvent kafkaEvent) {
        kafkaTemplate.send(RETRY_TOPIC, kafkaEvent);
    }
}
