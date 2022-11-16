package co.pragra.attendancesystem.repo;

import co.pragra.attendancesystem.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SessionRepo extends JpaRepository<Session, Long> {

    List<Session> searchSessionBySessionDate(Date sessionDate);
}
