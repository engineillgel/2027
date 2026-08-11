package edu.example.jobmgt.controller;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import edu.example.jobmgt.repository.UserRepository;
import edu.example.jobmgt.model.User;
import edu.example.jobmgt.security.JwtUtil;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder encoder;
  private final JwtUtil jwtUtil;

  @PostMapping("/register")
  public Map<String,Object> register(@RequestBody Map<String,String> body){
    String username = body.get("username");
    String password = body.get("password");
    String role = body.getOrDefault("role","STUDENT");
    if(username==null || password==null) return Map.of("error","username/password required");
    if(userRepository.findByUsername(username).isPresent()) return Map.of("error","username taken");
    User u = User.builder()
      .username(username)
      .password(encoder.encode(password))
      .role(role)
      .build();
    userRepository.save(u);
    return Map.of("message","ok");
  }

  @PostMapping("/login")
  public Map<String,Object> login(@RequestBody Map<String,String> body){
    String username = body.get("username");
    String password = body.get("password");
    var ou = userRepository.findByUsername(username);
    if(ou.isEmpty()) return Map.of("error","invalid");
    User u = ou.get();
    if(!encoder.matches(password, u.getPassword())) return Map.of("error","invalid");
    String token = jwtUtil.generateToken(u.getId(), u.getUsername(), u.getRole());
    return Map.of("token", token, "role", u.getRole());
  }
}
