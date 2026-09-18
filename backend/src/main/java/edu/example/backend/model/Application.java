package edu.example.backend.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Long jobId;
  @Column(nullable = false)
  private Long studentId;
  private String status = "APPLIED";
  private LocalDateTime appliedAt = LocalDateTime.now();
}
