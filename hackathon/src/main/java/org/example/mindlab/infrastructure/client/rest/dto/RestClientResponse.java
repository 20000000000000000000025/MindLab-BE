package org.example.mindlab.infrastructure.client.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.mindlab.domain.subject.Subject;

import java.util.List;

@Getter
@AllArgsConstructor
public class RestClientResponse {
  private String summation;
  private List<String> subject;
  private String problem;
  private String commentary;
}
