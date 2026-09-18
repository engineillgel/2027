package edu.example.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.example.backend.model.User;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  long countByRole(String role);
}
