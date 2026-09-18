package edu.example.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.example.backend.model.Notification;
import java.util.List;
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId);
  long deleteByStudentId(Long studentId);
}
