package _v3r.project.category.repository;

import _v3r.project.category.domain.Category;
import _v3r.project.prompt.domain.Prompt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    List<Category> findAllByPrompt(Prompt prompt);

    Optional<Category> findByPrompt(Prompt prompt);


}
