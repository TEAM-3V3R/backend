package _v3r.project.prompt.repository;

import _v3r.project.prompt.domain.Prompt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptRepository extends JpaRepository<Prompt,Long> {
    Optional<Prompt> findFirstByChatIdOrderByCreatedAtAsc(Long chatId); // 처음 프롬프트
    Optional<Prompt> findFirstByChatIdOrderByCreatedAtDesc(Long chatId); // 마지막 프롬프트
    List<Prompt> findAllByChatIdOrderByCreatedAtAsc(Long chatId);

}
