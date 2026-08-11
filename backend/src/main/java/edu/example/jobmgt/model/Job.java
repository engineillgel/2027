package edu.example.jobmgt.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Long employerId;
  @Column(nullable = false)
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;
  private String location;
  private LocalDateTime createdAt = LocalDateTime.now();
}
