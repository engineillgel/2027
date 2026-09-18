package edu.example.backend.service;

import edu.example.backend.model.Job;
import edu.example.backend.model.Schedule;
import edu.example.backend.model.User;
import edu.example.backend.repository.JobRepository;
import edu.example.backend.repository.ScheduleRepository;
import edu.example.backend.repository.UserRepository;
import edu.example.backend.security.AuthUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
  private final ScheduleRepository scheduleRepository;
  private final UserRepository userRepository;
  private final JobRepository jobRepository;

  public List<Map<String,Object>> list(){
    List<Map<String,Object>> result = new ArrayList<>();
    for(Schedule s : scheduleRepository.findAll()){
      Optional<User> ou = userRepository.findById(s.getStudentId());
      Optional<Job> oj = jobRepository.findById(s.getJobId());
      result.add(Map.of(
        "id", s.getId(),
        "studentId", s.getStudentId(),
        "studentName", ou.map(User::getRealname).orElse(""),
        "className", ou.map(User::getClassName).orElse(""),
        "jobId", s.getJobId(),
        "jobTitle", oj.map(Job::getTitle).orElse(""),
        "dayOfWeek", s.getDayOfWeek(),
        "startTime", s.getStartTime(),
        "endTime", s.getEndTime()
      ));
    }
    return result;
  }

  public List<Map<String,Object>> mySchedule(AuthUserPrincipal p){
    List<Map<String,Object>> result = new ArrayList<>();
    for(Schedule s : scheduleRepository.findByStudentId(p.getId())){
      Optional<Job> oj = jobRepository.findById(s.getJobId());
      result.add(Map.of(
        "id", s.getId(),
        "jobTitle", oj.map(Job::getTitle).orElse(""),
        "dayOfWeek", s.getDayOfWeek(),
        "startTime", s.getStartTime(),
        "endTime", s.getEndTime()
      ));
    }
    return result;
  }

  public Object create(Map<String,Object> body, AuthUserPrincipal p){
    if(!"ADMIN".equals(p.getRole())) return Map.of("error","forbidden");
    long studentId = ((Number) body.get("studentId")).longValue();
    long jobId = ((Number) body.get("jobId")).longValue();
    String dayOfWeek = (String) body.get("dayOfWeek");
    String startTime = (String) body.get("startTime");
    String endTime = (String) body.get("endTime");
    Integer start = parseMinutes(startTime);
    Integer end = parseMinutes(endTime);
    if(start == null || end == null){
      return Map.of("error","invalid_time","message","时间格式应为 HH:mm，例如 08:00");
    }
    if(end <= start){
      return Map.of("error","invalid_time","message","结束时间必须晚于开始时间");
    }
    // 排班冲突检测：同一学生、同一星期内，时间区间重叠则拒绝
    for(Schedule existing : scheduleRepository.findByStudentId(studentId)){
      if(dayOfWeek == null || !dayOfWeek.equals(existing.getDayOfWeek())){
        continue;
      }
      Integer es = parseMinutes(existing.getStartTime());
      Integer ee = parseMinutes(existing.getEndTime());
      if(es == null || ee == null){
        continue;
      }
      // 区间重叠判定：newStart < existingEnd && existingStart < newEnd
      if(start < ee && es < end){
        return Map.of("error","conflict","message",
          "排班冲突：该学生" + dayOfWeek + " " + existing.getStartTime() + "—" + existing.getEndTime() +
          "已有排班，请调整时间");
      }
    }
    Schedule s = Schedule.builder()
      .studentId(studentId)
      .jobId(jobId)
      .dayOfWeek(dayOfWeek)
      .startTime(startTime)
      .endTime(endTime)
      .build();
    scheduleRepository.save(s);
    return s;
  }

  public Object update(Long id, Map<String,Object> body, AuthUserPrincipal p){
    if(!"ADMIN".equals(p.getRole())) return Map.of("error","forbidden");
    Optional<Schedule> opt = scheduleRepository.findById(id);
    if(opt.isEmpty()){
      return Map.of("error","not_found","message","排班记录不存在");
    }
    Schedule s = opt.get();
    String dayOfWeek = (String) body.get("dayOfWeek");
    String startTime = (String) body.get("startTime");
    String endTime = (String) body.get("endTime");
    Integer start = parseMinutes(startTime);
    Integer end = parseMinutes(endTime);
    if(start == null || end == null){
      return Map.of("error","invalid_time","message","时间格式应为 HH:mm，例如 08:00");
    }
    if(end <= start){
      return Map.of("error","invalid_time","message","结束时间必须晚于开始时间");
    }
    if(dayOfWeek == null || dayOfWeek.isEmpty()){
      return Map.of("error","invalid_day","message","请选择星期");
    }
    // 排班冲突检测：同一学生、同一星期内，时间区间重叠则拒绝（排除本条自身）
    for(Schedule existing : scheduleRepository.findByStudentId(s.getStudentId())){
      if(existing.getId().equals(id)) continue;
      if(!dayOfWeek.equals(existing.getDayOfWeek())) continue;
      Integer es = parseMinutes(existing.getStartTime());
      Integer ee = parseMinutes(existing.getEndTime());
      if(es == null || ee == null) continue;
      if(start < ee && es < end){
        return Map.of("error","conflict","message",
          "排班冲突：该学生" + dayOfWeek + " " + existing.getStartTime() + "—" + existing.getEndTime() +
          "已有排班，请调整时间");
      }
    }
    s.setDayOfWeek(dayOfWeek);
    s.setStartTime(startTime);
    s.setEndTime(endTime);
    scheduleRepository.save(s);
    return s;
  }

  public Object delete(Long id, AuthUserPrincipal p){
    if(!"ADMIN".equals(p.getRole())) return Map.of("error","forbidden");
    if(!scheduleRepository.existsById(id)){
      return Map.of("error","not_found","message","排班记录不存在");
    }
    scheduleRepository.deleteById(id);
    return Map.of("ok", true, "id", id);
  }

  private Integer parseMinutes(String time){
    if(time == null){
      return null;
    }
    String[] parts = time.trim().split(":");
    if(parts.length != 2){
      return null;
    }
    try{
      int h = Integer.parseInt(parts[0].trim());
      int m = Integer.parseInt(parts[1].trim());
      if(h < 0 || h > 23 || m < 0 || m > 59){
        return null;
      }
      return h * 60 + m;
    }catch(NumberFormatException e){
      return null;
    }
  }
}
