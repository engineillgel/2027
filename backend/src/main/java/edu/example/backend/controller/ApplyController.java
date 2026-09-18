package edu.example.backend.controller;

import edu.example.backend.security.AuthUserPrincipal;
import edu.example.backend.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/apply")
@RequiredArgsConstructor
public class ApplyController {
  private final ApplyService applyService;

  @PostMapping("/{jobId}")
  public Object apply(@PathVariable Long jobId, Authentication authentication){
    return applyService.apply(jobId, (AuthUserPrincipal) authentication.getPrincipal());
  }

  @GetMapping("/my")
  public Object myApplications(Authentication authentication){
    return applyService.myApplications((AuthUserPrincipal) authentication.getPrincipal());
  }

  @GetMapping("/job/{jobId}")
  public Object byJob(@PathVariable Long jobId, Authentication authentication){
    return applyService.byJob(jobId, (AuthUserPrincipal) authentication.getPrincipal());
  }

  @GetMapping("/all")
  public Object all(Authentication authentication){
    return applyService.all((AuthUserPrincipal) authentication.getPrincipal());
  }

  @PutMapping("/{id}")
  public Object updateStatus(@PathVariable Long id, @RequestBody Map<String,String> body, Authentication authentication){
    return applyService.updateStatus(id, body, (AuthUserPrincipal) authentication.getPrincipal());
  }
}
