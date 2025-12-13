package _v3r.project.report.repository;

import _v3r.project.report.domain.Report;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {
    // 해당 채팅방의 리포트 찾기
    Optional<Report> findByChat_ChatId(Long chatId);

}
