package edu.example.jobmgt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.example.jobmgt.model.Application;
import java.util.List;
public interface ApplicationRepository extends JpaRepository<Application, Long> {
  List<Application> findByStudentId(Long studentId);
  List<Application> findByJobId(Long jobId);
}
