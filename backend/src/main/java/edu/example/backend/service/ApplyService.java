package edu.example.backend.service;

import edu.example.backend.model.Application;
import edu.example.backend.model.Job;
import edu.example.backend.model.User;
import edu.example.backend.repository.ApplicationRepository;
import edu.example.backend.repository.JobRepository;
import edu.example.backend.repository.UserRepository;
import edu.example.backend.security.AuthUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplyService {
  private final ApplicationRepository applicationRepository;
  private final JobRepository jobRepository;
  private final UserRepository userRepository;

  public Object apply(Long jobId, AuthUserPrincipal p){
    if(!"STUDENT".equals(p.getRole())) return Map.of("error","only students can apply");
    Optional<Job> oj = jobRepository.findById(jobId);
    if(oj.isEmpty()) return Map.of("error","job not found");
    Application a = Application.builder()
      .jobId(jobId).studentId(p.getId())
      .status("APPLIED").appliedAt(LocalDateTime.now())
      .build();
    applicationRepository.save(a);
    return Map.of("message","applied");
  }

  public Object myApplications(AuthUserPrincipal p){
    List<Map<String,Object>> result = new ArrayList<>();
    for(Application a : applicationRepository.findByStudentIdOrderByAppliedAtDesc(p.getId())){
      Optional<Job> oj = jobRepository.findById(a.getJobId());
      result.add(Map.of(
        "id", a.getId(),
        "jobId", a.getJobId(),
        "jobTitle", oj.map(Job::getTitle).orElse(""),
        "status", a.getStatus() == null ? "APPLIED" : a.getStatus(),
        "appliedAt", a.getAppliedAt() == null ? String.valueOf(LocalDateTime.now()) : String.valueOf(a.getAppliedAt())
      ));
    }
    return result;
  }

  public Object byJob(Long jobId, AuthUserPrincipal p){
    Optional<Job> oj = jobRepository.findById(jobId);
    if(oj.isEmpty()) return Map.of("error","job not found");
    Job job = oj.get();
    if(!"ADMIN".equals(p.getRole())){
      return Map.of("error","forbidden");
    }
    List<Map<String,Object>> result = new ArrayList<>();
    for(Application a : applicationRepository.findByJobId(jobId)){
      Optional<User> ou = userRepository.findById(a.getStudentId());
      result.add(Map.of(
        "id", a.getId(),
        "jobId", a.getJobId(),
        "jobTitle", job.getTitle(),
        "studentId", a.getStudentId(),
        "studentName", ou.map(User::getRealname).orElse(""),
        "className", ou.map(User::getClassName).orElse(""),
        "status", a.getStatus() == null ? "APPLIED" : a.getStatus(),
        "appliedAt", a.getAppliedAt() == null ? String.valueOf(LocalDateTime.now()) : String.valueOf(a.getAppliedAt())
      ));
    }
    return result;
  }

  // 全部申请：仅管理员可查看（申请管理已并入管理员角色）
  public Object all(AuthUserPrincipal p){
    if(!"ADMIN".equals(p.getRole())) return Map.of("error","forbidden");
    List<Application> apps = applicationRepository.findAllByOrderByAppliedAtDesc();
    List<Map<String,Object>> result = new ArrayList<>();
    for(Application a : apps){
      Optional<Job> oj = jobRepository.findById(a.getJobId());
      Optional<User> ou = userRepository.findById(a.getStudentId());
      result.add(Map.of(
        "id", a.getId(),
        "jobId", a.getJobId(),
        "jobTitle", oj.map(Job::getTitle).orElse(""),
        "studentId", a.getStudentId(),
        "studentName", ou.map(User::getRealname).orElse(""),
        "className", ou.map(User::getClassName).orElse(""),
        "status", a.getStatus() == null ? "APPLIED" : a.getStatus(),
        "appliedAt", a.getAppliedAt() == null ? String.valueOf(LocalDateTime.now()) : String.valueOf(a.getAppliedAt())
      ));
    }
    return result;
  }

  public Object updateStatus(Long id, Map<String,String> body, AuthUserPrincipal p){
    if(!"ADMIN".equals(p.getRole())) return Map.of("error","forbidden");
    Optional<Application> oa = applicationRepository.findById(id);
    if(oa.isEmpty()) return Map.of("error","not found");
    Application a = oa.get();
    a.setStatus(body.getOrDefault("status","APPLIED"));
    applicationRepository.save(a);
    return Map.of("message","updated");
  }
}
