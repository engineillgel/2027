package edu.example.backend.service;

import edu.example.backend.model.Notification;
import edu.example.backend.repository.NotificationRepository;
import edu.example.backend.security.AuthUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;

  // 当前学生的全部通知（管理员删除岗位后由系统写入）
  public List<Map<String,Object>> my(AuthUserPrincipal p){
    List<Map<String,Object>> result = new ArrayList<>();
    for(Notification n : notificationRepository.findByStudentIdOrderByCreatedAtDesc(p.getId())){
      result.add(Map.of(
        "id", n.getId(),
        "content", n.getContent(),
        "createdAt", String.valueOf(n.getCreatedAt())
      ));
    }
    return result;
  }

  // 学生确认弹窗后清除本人的全部通知（派生 deleteBy 需事务）
  @Transactional
  public Map<String,Object> clear(AuthUserPrincipal p){
    notificationRepository.deleteByStudentId(p.getId());
    return Map.of("ok", true);
  }
}
