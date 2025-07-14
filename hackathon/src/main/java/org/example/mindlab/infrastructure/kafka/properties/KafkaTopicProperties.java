package org.example.mindlab.infrastructure.kafka.properties;

import java.util.List;

public class KafkaTopicProperties {

    public static final String INCREASE_VIEW_TOPIC = "increase-view";

    public static final String DEAD_LETTER_TOPIC = "post-dlq";

    public static final String RETRY_TOPIC = "retry";

    public static final List<String> RETRY_TARGET_TOPICS = List.of(
        INCREASE_VIEW_TOPIC,
        DEAD_LETTER_TOPIC,
        RETRY_TOPIC
    );
}
