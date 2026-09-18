package edu.example.backend.service;

import edu.example.backend.model.Job;
import edu.example.backend.repository.ApplicationRepository;
import edu.example.backend.repository.JobRepository;
import edu.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {
  private final JobRepository jobRepository;
  private final ApplicationRepository applicationRepository;
  private final UserRepository userRepository;

  public Map<String,Object> overview(){
    long totalJobs = jobRepository.count();
    long totalStudents = userRepository.countByRole("STUDENT");
    long totalApplications = applicationRepository.count();
    long applied = applicationRepository.countByStatus("APPLIED");
    long accepted = applicationRepository.countByStatus("ACCEPTED");
    long rejected = applicationRepository.countByStatus("REJECTED");

    List<Map<String,Object>> byStatus = new ArrayList<>();
    byStatus.add(Map.of("status","已申请","count",applied));
    byStatus.add(Map.of("status","已录用","count",accepted));
    byStatus.add(Map.of("status","已拒绝","count",rejected));

    List<Map<String,Object>> byJob = new ArrayList<>();
    for(Job j : jobRepository.findAll()){
      byJob.add(Map.of("jobTitle", j.getTitle(), "count", applicationRepository.countByJobId(j.getId())));
    }
    return Map.of(
      "totalJobs", totalJobs,
      "totalStudents", totalStudents,
      "totalApplications", totalApplications,
      "byStatus", byStatus,
      "byJob", byJob
    );
  }
}
