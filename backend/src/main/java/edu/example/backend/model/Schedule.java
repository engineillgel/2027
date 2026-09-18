package edu.example.backend.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Long studentId;
  @Column(nullable = false)
  private Long jobId;
  @Column(nullable = false, length = 10)
  private String dayOfWeek;   // 星期，如 周一
  @Column(nullable = false, length = 10)
  private String startTime;   // 开始时间，如 08:00
  @Column(nullable = false, length = 10)
  private String endTime;     // 结束时间，如 12:00
}
