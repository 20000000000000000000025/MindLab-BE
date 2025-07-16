package org.example.mindlab.infrastructure.client.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mindlab.infrastructure.client.rest.dto.RestClientResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestClient {

  private static final String AI_URL = "https://mindlab-ai.onrender.com/ocr";
  //private static final String AI_URL = "http://192.168.1.14:8080/ocr";

  private final ObjectMapper objectMapper;
  private final RestTemplate restTemplate = new RestTemplate();

  public RestClientResponse sendImageToAi(MultipartFile file) throws IOException {
    // MultipartFile을 ByteArrayResource로 변환
    ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
      @Override
      public String getFilename() {
        return file.getOriginalFilename();
      }
    };

    // multipart/form-data 요청 본문 생성
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("img", fileResource);

    // 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    // 요청 엔티티 생성
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // API 호출
    ResponseEntity<String> response = restTemplate.postForEntity(AI_URL, requestEntity, String.class);

    if (response.getStatusCode() != HttpStatus.OK) {
      log.error("AI 서버 응답 오류: {}", response.getStatusCode());
      throw new RuntimeException("AI 서버 오류: " + response.getStatusCode());
    }

    // JSON 파싱
    String json = response.getBody();
    JsonNode node = objectMapper.readTree(json);

    String summation = node.hasNonNull("summary") ? node.get("summary").asText() : "";

    JsonNode subjectNode = node.get("subject");
    List<String> subjectList;
    if (subjectNode != null) {
      if (subjectNode.isArray()) {
        subjectList = objectMapper.convertValue(
            subjectNode,
            objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );
      } else {
        subjectList = List.of(subjectNode.asText());
      }
    } else {
      subjectList = List.of();
    }

    String problem = node.hasNonNull("question") ? node.get("question").asText() : "";
    String commentary = node.hasNonNull("explanation") ? node.get("explanation").asText() : "";

    log.info("AI 서버 요청 성공: 파일={}, 과목수={}", file.getOriginalFilename(), subjectList.size());

    return new RestClientResponse(summation, subjectList, problem, commentary);
  }
}
