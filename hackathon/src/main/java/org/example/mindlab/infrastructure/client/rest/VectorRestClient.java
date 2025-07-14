package org.example.mindlab.infrastructure.client.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class VectorRestClient { // todo 사실 벡터 생성기는 아니고 http 통신할일 있을 것 같아서 남겨놓은 놈

    private final static String URL = "http://localhost:5000/embed";

    private final ObjectMapper objectMapper;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public void generateVector(String input) {
        try {
            Map<String, String> requestBody = Map.of("sentence", input);
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpUriRequest request = RequestBuilder.post(URL)
                .setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)))
                .setHeader("X-Trace-Id", "some-trace-id")
                .setHeader("Accept", "application/json")
                .build();

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
