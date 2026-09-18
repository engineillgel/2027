package edu.example.backend.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {

  @Autowired
  private JwtUtil jwtUtil;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(List.of("*"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil);
    http.csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(auth -> auth
          .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
          .requestMatchers("/error").permitAll()
          .requestMatchers("/api/public/**").permitAll()
          .requestMatchers("/api/admin/**").hasRole("ADMIN")
          .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }
}
