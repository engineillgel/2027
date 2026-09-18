package edu.example.backend.controller;

import edu.example.backend.security.AuthUserPrincipal;
import edu.example.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public Map<String,Object> register(@RequestBody Map<String,String> body){
    return authService.register(body);
  }

  @PostMapping("/login")
  public Map<String,Object> login(@RequestBody Map<String,String> body){
    return authService.login(body);
  }

  @GetMapping("/me")
  public Map<String,Object> me(Authentication authentication){
    return authService.me((AuthUserPrincipal) authentication.getPrincipal());
  }
}
