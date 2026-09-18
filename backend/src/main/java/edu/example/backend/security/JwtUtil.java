package edu.example.backend.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
  private final Key key;
  private final long expirationMs;

  public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationMs){
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.expirationMs = expirationMs;
  }

  public String generateToken(Long userId, String username, String role){
    return Jwts.builder()
      .setSubject(username)
      .addClaims(Map.of("id", userId, "role", role))
      .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
      .signWith(key)
      .compact();
  }

  public Jws<Claims> parseToken(String token){
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
  }
}
