package edu.example.jobmgt.controller;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import edu.example.jobmgt.repository.JobRepository;
import edu.example.jobmgt.model.Job;
import org.springframework.security.core.Authentication;
import edu.example.jobmgt.security.AuthUserPrincipal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
  private final JobRepository jobRepository;

  @GetMapping
  public List<Job> list() { return jobRepository.findAll(); }

  @PostMapping
  public Object create(@RequestBody Map<String,String> body, Authentication authentication){
    AuthUserPrincipal p = (AuthUserPrincipal) authentication.getPrincipal();
    if(!"EMPLOYER".equals(p.getRole()) && !"ADMIN".equals(p.getRole())){
      return Map.of("error","forbidden");
    }
    Job j = Job.builder()
      .employerId(p.getId())
      .title(body.get("title"))
      .description(body.get("description"))
      .location(body.get("location"))
      .build();
    jobRepository.save(j);
    return j;
  }

  @GetMapping("/{id}")
  public Object get(@PathVariable Long id){
    return jobRepository.findById(id).orElse(Map.of("error","not found"));
  }

  @DeleteMapping("/{id}")
  public Object delete(@PathVariable Long id, Authentication authentication){
    AuthUserPrincipal p = (AuthUserPrincipal) authentication.getPrincipal();
    var oj = jobRepository.findById(id);
    if(oj.isEmpty()) return Map.of("error","not found");
    Job j = oj.get();
    if(!p.getId().equals(j.getEmployerId()) && !"ADMIN".equals(p.getRole())) return Map.of("error","forbidden");
    jobRepository.deleteById(id);
    return Map.of("message","deleted");
  }
}
