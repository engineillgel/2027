package edu.example.jobmgt.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  @Autowired
  private JwtUtil jwtUtil;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil);
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
          .requestMatchers("/api/auth/**").permitAll()
          .requestMatchers("/api/public/**").permitAll()
          .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }
}
