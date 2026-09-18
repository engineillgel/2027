package edu.example.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.example.backend.model.Schedule;
import java.util.List;
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
  List<Schedule> findByStudentId(Long studentId);
  List<Schedule> findByJobId(Long jobId);
  // 级联删除：删除岗位时清除其全部排班记录
  void deleteByJobId(Long jobId);
}
