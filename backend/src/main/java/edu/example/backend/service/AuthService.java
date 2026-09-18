package edu.example.backend.service;

import edu.example.backend.model.User;
import edu.example.backend.repository.UserRepository;
import edu.example.backend.security.AuthUserPrincipal;
import edu.example.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder encoder;
  private final JwtUtil jwtUtil;

  public Map<String,Object> register(Map<String,String> body){
    String username = body.get("username");
    String password = body.get("password");
    if(username==null || password==null) return Map.of("error","username/password required");
    if(userRepository.findByUsername(username).isPresent()) return Map.of("error","username taken");
    // 自注册固定为学生：系统仅 STUDENT/ADMIN 两角色，管理员账号由数据库/种子创建
    User u = User.builder()
      .username(username)
      .password(encoder.encode(password))
      .role("STUDENT")
      .enabled(true)
      .build();
    userRepository.save(u);
    return Map.of("message","ok");
  }

  public Map<String,Object> login(Map<String,String> body){
    String username = body.get("username");
    String password = body.get("password");
    var ou = userRepository.findByUsername(username);
    if(ou.isEmpty()) return Map.of("error","invalid");
    User u = ou.get();
    if(!encoder.matches(password, u.getPassword())) return Map.of("error","invalid");
    if(u.getEnabled() != null && !u.getEnabled()) return Map.of("error","disabled");
    String token = jwtUtil.generateToken(u.getId(), u.getUsername(), u.getRole());
    return Map.of("token", token, "role", u.getRole());
  }

  public Map<String,Object> me(AuthUserPrincipal p){
    User u = userRepository.findById(p.getId()).orElse(null);
    if(u == null) return Map.of("error","user not found");
    return Map.of(
      "id", u.getId(),
      "username", u.getUsername(),
      "role", u.getRole(),
      "realname", u.getRealname() == null ? "" : u.getRealname(),
      "className", u.getClassName() == null ? "" : u.getClassName()
    );
  }
}
