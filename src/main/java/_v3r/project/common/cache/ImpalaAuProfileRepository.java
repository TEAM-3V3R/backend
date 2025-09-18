package _v3r.project.common.cache;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImpalaAuProfileRepository extends JpaRepository<ImpalaAuProfile, Long> {

    @Query("SELECT COUNT(a) FROM ImpalaAuProfile a WHERE a.date = :date")
    Long getTotalAu(@Param("date") String date);

    @Query("SELECT COUNT(u) FROM ImpalaAuProfile u WHERE u.date = :date")
    Long getTotalUser(@Param("date") String date);
}

