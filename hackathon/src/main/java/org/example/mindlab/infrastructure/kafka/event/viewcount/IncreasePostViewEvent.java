package org.example.mindlab.infrastructure.kafka.event.viewcount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncreasePostViewEvent {

    private Long postId;

    private Long userId;
}
