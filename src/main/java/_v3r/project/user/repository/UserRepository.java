package _v3r.project.user.repository;

import _v3r.project.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByIdName(String idName);
    boolean existsByIdName(String IdName);

}
