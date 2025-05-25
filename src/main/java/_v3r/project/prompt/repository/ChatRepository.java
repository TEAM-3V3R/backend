package _v3r.project.prompt.repository;

import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.enumtype.Paints;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatRepository extends JpaRepository<Chat,Long>, JpaSpecificationExecutor<Chat> {
    List<Chat> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    List<Chat> findAllByUserIdAndPaintsOrderByCreatedAtDesc(Long userId, Paints paints);




}
