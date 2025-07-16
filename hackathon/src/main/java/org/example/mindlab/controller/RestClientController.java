package org.example.mindlab.controller;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.infrastructure.client.rest.AIRestClient;
import org.example.mindlab.infrastructure.client.rest.dto.RestClientResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class RestClientController {

  private final AIRestClient aiRestClient;

  @PostMapping("/ai")
  public RestClientResponse ai(@RequestParam("file") MultipartFile file) throws IOException {
    return aiRestClient.sendImageToAi(file);
  }
}
