package _v3r.project.prompt.repository;

import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatRepository extends JpaRepository<Chat,Long>, JpaSpecificationExecutor<Chat> {

    Optional<Chat> findByUserIdAndId(Long userId, Long chatId);

    List<Chat> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    List<Chat> findAllByUserIdAndPaintsOrderByCreatedAtDesc(Long userId, Paints paints);




}
