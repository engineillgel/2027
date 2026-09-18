package edu.example.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import edu.example.backend.model.Job;
import java.util.List;
public interface JobRepository extends JpaRepository<Job, Long> {
  List<Job> findByEmployerId(Long employerId);
  Page<Job> findByTitleContainingOrDescriptionContaining(String title, String description, Pageable pageable);
}
