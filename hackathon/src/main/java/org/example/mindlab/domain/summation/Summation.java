package org.example.mindlab.domain.summation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="tbl_summation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Summation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  private String title;

  private String content;

  private String problem;

  private String feedback;
}