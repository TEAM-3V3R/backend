package _v3r.project.prompt.repository;

import _v3r.project.prompt.domain.Prompt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptRepository extends JpaRepository<Prompt,Long> {
    Optional<Prompt> findFirstByChatChatIdOrderByCreatedAtAsc(Long chatId); // 처음 프롬프트
    Optional<Prompt> findFirstByChatChatIdOrderByCreatedAtDesc(Long chatId); // 마지막 프롬프트
    List<Prompt> findAllByChatChatIdOrderByCreatedAtAsc(Long chatId);

    @Query(value = "SELECT image_url FROM prompt WHERE chat_id = :chatId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<String> findLastResultImageUrlByChatIdNative(@Param("chatId") Long chatId);


}
