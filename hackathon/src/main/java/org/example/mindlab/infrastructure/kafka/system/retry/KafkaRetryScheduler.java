package org.example.mindlab.infrastructure.kafka.system.retry;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mindlab.infrastructure.kafka.dto.KafkaEvent;
import org.example.mindlab.infrastructure.kafka.util.JsonSerializer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static org.example.mindlab.infrastructure.kafka.properties.KafkaTopicProperties.RETRY_TARGET_TOPICS;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaRetryScheduler {

    private final RedisTemplate<String, String> redisTemplate;

    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    private final JsonSerializer jsonConverter;

    @Scheduled(fixedDelay = 30000)
    public void processShortQueue() {
        for (String topic : RETRY_TARGET_TOPICS) {
            process("redis:short:" + topic);
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void processLongQueue() {
        for (String topic : RETRY_TARGET_TOPICS) {
            process("redis:long:" + topic);
        }
    }

    private void process(String key) {
        long now = System.currentTimeMillis();
        Set<String> messages = redisTemplate.opsForZSet().rangeByScore(key, 0, now);

        for (String kafkaEventJson : messages) {
            redisTemplate.opsForZSet().remove(key, kafkaEventJson);

            KafkaEvent kafkaEvent =
                jsonConverter.fromJson(kafkaEventJson, KafkaEvent.class);

            kafkaTemplate.send(kafkaEvent.getTopic(), kafkaEvent);
        }
    }
}

