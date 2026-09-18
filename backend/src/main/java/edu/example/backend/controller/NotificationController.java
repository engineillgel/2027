package edu.example.backend.controller;

import edu.example.backend.security.AuthUserPrincipal;
import edu.example.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
  private final NotificationService notificationService;

  @GetMapping("/my")
  public List<Map<String,Object>> my(Authentication authentication){
    return notificationService.my((AuthUserPrincipal) authentication.getPrincipal());
  }

  @DeleteMapping("/my")
  public Map<String,Object> clear(Authentication authentication){
    return notificationService.clear((AuthUserPrincipal) authentication.getPrincipal());
  }
}
