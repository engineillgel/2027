package edu.example.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.example.backend.model.Application;
import java.util.List;
public interface ApplicationRepository extends JpaRepository<Application, Long> {
  List<Application> findByStudentId(Long studentId);
  List<Application> findByStudentIdOrderByAppliedAtDesc(Long studentId);
  List<Application> findByJobId(Long jobId);
  List<Application> findByJobIdInOrderByAppliedAtDesc(List<Long> jobIds);
  List<Application> findAllByOrderByAppliedAtDesc();
  long countByStatus(String status);
  long countByJobId(Long jobId);
  // 级联删除：删除岗位时清除其全部申请记录
  void deleteByJobId(Long jobId);
}
