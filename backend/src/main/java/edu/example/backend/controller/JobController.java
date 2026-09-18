package edu.example.backend.controller;

import edu.example.backend.model.Job;
import edu.example.backend.security.AuthUserPrincipal;
import edu.example.backend.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
  private final JobService jobService;

  @GetMapping
  public Page<Job> list(@RequestParam(defaultValue="0") int page,
                        @RequestParam(defaultValue="10") int size,
                        @RequestParam(required=false) String q) {
    return jobService.list(page, size, q);
  }

  @GetMapping("/mine")
  public List<Job> mine(Authentication authentication){
    return jobService.mine((AuthUserPrincipal) authentication.getPrincipal());
  }

  @PostMapping
  public Object create(@RequestBody Map<String,String> body, Authentication authentication){
    return jobService.create((AuthUserPrincipal) authentication.getPrincipal(), body);
  }

  @GetMapping("/{id}")
  public Object get(@PathVariable Long id){
    return jobService.get(id);
  }

  @DeleteMapping("/{id}")
  public Object delete(@PathVariable Long id, Authentication authentication){
    return jobService.delete((AuthUserPrincipal) authentication.getPrincipal(), id);
  }
}
