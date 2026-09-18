package edu.example.backend.controller;

import edu.example.backend.security.AuthUserPrincipal;
import edu.example.backend.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
  private final ScheduleService scheduleService;

  @GetMapping
  public List<Map<String,Object>> list(){
    return scheduleService.list();
  }

  @GetMapping("/my")
  public List<Map<String,Object>> mySchedule(Authentication authentication){
    // /api/schedules/** 已放行，匿名用户访问时防御性返回空列表
    if(!(authentication.getPrincipal() instanceof AuthUserPrincipal)){
      return List.of();
    }
    return scheduleService.mySchedule((AuthUserPrincipal) authentication.getPrincipal());
  }

  @PostMapping
  public Object create(@RequestBody Map<String,Object> body, Authentication authentication){
    return scheduleService.create(body, (AuthUserPrincipal) authentication.getPrincipal());
  }

  @PutMapping("/{id}")
  public Object update(@PathVariable Long id, @RequestBody Map<String,Object> body, Authentication authentication){
    return scheduleService.update(id, body, (AuthUserPrincipal) authentication.getPrincipal());
  }

  @DeleteMapping("/{id}")
  public Object delete(@PathVariable Long id, Authentication authentication){
    return scheduleService.delete(id, (AuthUserPrincipal) authentication.getPrincipal());
  }
}
