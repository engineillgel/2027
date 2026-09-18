package edu.example.backend.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil){ this.jwtUtil = jwtUtil; }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if(header != null && header.startsWith("Bearer ")){
      String token = header.substring(7);
      try {
        var claimsJws = jwtUtil.parseToken(token);
        Claims claims = claimsJws.getBody();
        String username = claims.getSubject();
        Map<String,Object> map = claims;
        String role = (String) map.get("role");
        Long id = ((Number) map.get("id")).longValue();
        var auth = new UsernamePasswordAuthenticationToken(
          new AuthUserPrincipal(id, username, role),
          null,
          List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch(Exception ex){
        // invalid token -> ignore
      }
    }
    filterChain.doFilter(request, response);
  }
}
