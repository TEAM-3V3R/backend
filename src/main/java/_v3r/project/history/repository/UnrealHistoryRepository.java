package _v3r.project.history.repository;

import _v3r.project.history.domain.UnrealHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnrealHistoryRepository extends JpaRepository<UnrealHistory,Long> {
    List<UnrealHistory> findAllByUser_UserIdAndChat_ChatIdOrderByTimestampAsc(Long userId, Long chatId);


}
