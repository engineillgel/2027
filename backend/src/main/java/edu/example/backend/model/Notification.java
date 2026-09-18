package edu.example.backend.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Long studentId;
  @Column(nullable = false, length = 500)
  private String content;
  private LocalDateTime createdAt = LocalDateTime.now();
}
