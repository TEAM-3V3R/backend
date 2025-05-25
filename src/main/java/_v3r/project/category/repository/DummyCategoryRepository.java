package _v3r.project.category.repository;

import _v3r.project.category.domain.DummyCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DummyCategoryRepository extends JpaRepository<DummyCategory,Long> {

    Optional<DummyCategory> findByCategoryCombination(String categoryCombination);
}
