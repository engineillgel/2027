package edu.example.jobmgt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.example.jobmgt.model.User;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
