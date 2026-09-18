package edu.example.backend.security;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class AuthUserPrincipal {
  private Long id;
  private String username;
  private String role;
}
