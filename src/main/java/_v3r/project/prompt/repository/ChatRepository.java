package _v3r.project.prompt.repository;

import _v3r.project.prompt.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat,Long> {

}
