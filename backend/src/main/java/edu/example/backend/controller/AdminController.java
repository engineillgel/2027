package edu.example.backend.controller;

import edu.example.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
  private final AdminService adminService;

  // 仅 ADMIN 可访问（SecurityConfig 中 hasRole("ADMIN")）
  @GetMapping("/users")
  public List<Map<String,Object>> users(){
    return adminService.users();
  }

  // body 可含 enabled(true/false) 和/或 password(重置密码)
  @PutMapping("/users/{id}")
  public Map<String,Object> updateUser(@PathVariable Long id, @RequestBody Map<String,String> body){
    return adminService.updateUser(id, body);
  }
}
