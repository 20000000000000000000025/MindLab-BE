package org.example.mindlab.infrastructure.kafka.summary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mindlab.domain.subject.Subject;
import org.example.mindlab.domain.subject.repository.SubjectRepository;
import org.example.mindlab.domain.summation.Summation;
import org.example.mindlab.domain.summation.repository.SummationRepository;
import org.example.mindlab.infrastructure.client.rest.AIRestClient;
import org.example.mindlab.infrastructure.client.rest.dto.RestClientResponse;
import org.example.mindlab.infrastructure.kafka.properties.KafkaProperties;
import org.example.mindlab.infrastructure.s3.service.S3Service;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

import static org.example.mindlab.infrastructure.kafka.properties.KafkaProperties.GROUP_ID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryConsumer {

    private final SummationRepository summationRepository;

    private final AIRestClient aiRestClient;

    private final S3Service s3Service;
    
    private final SubjectRepository subjectRepository;


    @KafkaListener(
        topics = "summary_create",
        groupId = GROUP_ID,
        containerFactory = KafkaProperties.CONTAINER_FACTORY
    )
    public void consume(SummaryCreateEvent event, Acknowledgment ack) {
        try {
            MultipartFile multipartFile = s3Service.load(event.getSummaryId());
            RestClientResponse response = aiRestClient.sendImageToAi(multipartFile);

            Summation summation = Summation.builder()
                .id(event.getSummaryId())
                .content(response.getSummation())
                .problem(response.getProblem())
                .feedback(response.getCommentary())
                .build();

            List<String> subjectList = response.getSubject();

            if (subjectList != null && !subjectList.isEmpty()) {
                for (String subjectName : subjectList) {
                    Subject subject = Subject.builder()
                        .summation(summation)
                        .name(subjectName)
                        .build();
                    subjectRepository.save(subject);
                }
            }
            summationRepository.save(summation);
            log.info("생성 성공!!");

            ack.acknowledge();
        } catch (Exception e) {
            log.error("요약 생성 실패");
        }
    }
}
