package edu.example.jobmgt.controller;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import edu.example.jobmgt.repository.ApplicationRepository;
import edu.example.jobmgt.repository.JobRepository;
import edu.example.jobmgt.model.Application;
import edu.example.jobmgt.model.Job;
import org.springframework.security.core.Authentication;
import edu.example.jobmgt.security.AuthUserPrincipal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/apply")
@RequiredArgsConstructor
public class ApplyController {
  private final ApplicationRepository applicationRepository;
  private final JobRepository jobRepository;

  @PostMapping("/{jobId}")
  public Object apply(@PathVariable Long jobId, Authentication authentication){
    AuthUserPrincipal p = (AuthUserPrincipal) authentication.getPrincipal();
    if(!"STUDENT".equals(p.getRole())) return Map.of("error","only students can apply");
    Optional<Job> oj = jobRepository.findById(jobId);
    if(oj.isEmpty()) return Map.of("error","job not found");
    Application a = Application.builder().jobId(jobId).studentId(p.getId()).build();
    applicationRepository.save(a);
    return Map.of("message","applied");
  }

  @GetMapping("/my")
  public Object myApplications(Authentication authentication){
    AuthUserPrincipal p = (AuthUserPrincipal) authentication.getPrincipal();
    return applicationRepository.findByStudentId(p.getId());
  }
}
