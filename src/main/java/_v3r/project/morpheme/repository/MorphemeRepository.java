package _v3r.project.morpheme.repository;

import _v3r.project.morpheme.domain.Morpheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MorphemeRepository extends JpaRepository<Morpheme,Long> {

}
