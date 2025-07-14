package org.example.mindlab.domain.subject;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.mindlab.domain.summation.Summation;

@Entity
@Table(name = "tbl_subject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "summation_id")
  private Summation summation;

  @Column(length = 10, nullable = false)
  private String name;
}