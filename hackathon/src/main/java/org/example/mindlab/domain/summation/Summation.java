package org.example.mindlab.domain.summation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="tbl_summation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Summation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String content;

  private String problem;

  private String feedback;


}