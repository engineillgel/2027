package edu.example.backend.service;

import edu.example.backend.model.Job;
import edu.example.backend.model.Notification;
import edu.example.backend.repository.ApplicationRepository;
import edu.example.backend.repository.JobRepository;
import edu.example.backend.repository.NotificationRepository;
import edu.example.backend.repository.ScheduleRepository;
import edu.example.backend.security.AuthUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JobService {
  private final JobRepository jobRepository;
  private final ApplicationRepository applicationRepository;
  private final ScheduleRepository scheduleRepository;
  private final NotificationRepository notificationRepository;

  public Page<Job> list(int page, int size, String q){
    PageRequest pageable = PageRequest.of(page, size);
    if(q != null && !q.isBlank()){
      return jobRepository.findByTitleContainingOrDescriptionContaining(q, q, pageable);
    }
    return jobRepository.findAll(pageable);
  }

  public List<Job> mine(AuthUserPrincipal p){
    // 仅管理员可查全部岗位（发布岗位功能已并入管理员）
    if(!"ADMIN".equals(p.getRole())) return List.of();
    return jobRepository.findAll();
  }

  public Object create(AuthUserPrincipal p, Map<String,String> body){
    if(!"ADMIN".equals(p.getRole())){
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

  public Object get(Long id){
    return jobRepository.findById(id)
      .map(j -> (Object) j)
      .orElse(Map.of("error","not found"));
  }

  // 级联删除：删除岗位时，其全部申请/排班记录一并清除，并给受影响的学生写入登录提醒
  @Transactional
  public Object delete(AuthUserPrincipal p, Long id){
    var oj = jobRepository.findById(id);
    if(oj.isEmpty()) return Map.of("error","not found");
    Job j = oj.get();
    if(!"ADMIN".equals(p.getRole())) return Map.of("error","forbidden");

    // 通知已申请该岗位的学生：申请记录被清除
    for(var app : applicationRepository.findByJobId(id)){
      notificationRepository.save(Notification.builder()
        .studentId(app.getStudentId())
        .content("你申请的岗位「" + j.getTitle() + "」已被管理员删除，相关申请记录已清除。")
        .createdAt(LocalDateTime.now())
        .build());
    }
    // 通知被安排该岗位的学生：排班记录被清除
    for(var sched : scheduleRepository.findByJobId(id)){
      notificationRepository.save(Notification.builder()
        .studentId(sched.getStudentId())
        .content("你被安排的岗位「" + j.getTitle() + "」已被管理员删除，相关排班记录已清除。")
        .createdAt(LocalDateTime.now())
        .build());
    }

    applicationRepository.deleteByJobId(id);
    scheduleRepository.deleteByJobId(id);
    jobRepository.deleteById(id);
    return Map.of("message","deleted");
  }
}
