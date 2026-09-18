package edu.example.backend.service;

import edu.example.backend.model.User;
import edu.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder encoder;

  public List<Map<String,Object>> users(){
    List<Map<String,Object>> result = new ArrayList<>();
    for(User u : userRepository.findAll()){
      result.add(Map.of(
        "id", u.getId(),
        "username", u.getUsername(),
        "realname", u.getRealname() == null ? "" : u.getRealname(),
        "className", u.getClassName() == null ? "" : u.getClassName(),
        "role", u.getRole(),
        "enabled", u.getEnabled() == null || u.getEnabled()
      ));
    }
    return result;
  }

  public Map<String,Object> updateUser(Long id, Map<String,String> body){
    Optional<User> ou = userRepository.findById(id);
    if(ou.isEmpty()) return Map.of("error","not found");
    User u = ou.get();
    if(body.containsKey("enabled")){
      // 超级管理员账号不可禁用，避免把自己锁死在系统外
      if("admin".equals(u.getUsername())){
        return Map.of("error","cannot disable admin");
      }
      u.setEnabled(Boolean.parseBoolean(body.get("enabled")));
    }
    String pwd = body.get("password");
    if(pwd != null && !pwd.isBlank()){
      u.setPassword(encoder.encode(pwd));
    }
    userRepository.save(u);
    return Map.of("message","updated");
  }
}
