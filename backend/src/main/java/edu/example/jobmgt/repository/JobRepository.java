package edu.example.jobmgt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.example.jobmgt.model.Job;
import java.util.List;
public interface JobRepository extends JpaRepository<Job, Long> {
  List<Job> findByEmployerId(Long employerId);
}
