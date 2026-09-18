package edu.example.backend.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private String username;
  @Column(nullable = false)
  private String password;
  @Column(nullable = false)
  private String role; // STUDENT, ADMIN
  private String realname;
  private String className; // 班级，如 2024软件工程一班
  @Builder.Default
  private Boolean enabled = true; // 账号是否启用（false=禁用，无法登录）
  private LocalDateTime createdAt = LocalDateTime.now();
}
