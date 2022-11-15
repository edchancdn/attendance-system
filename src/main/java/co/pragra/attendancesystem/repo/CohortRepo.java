package co.pragra.attendancesystem.repo;

import co.pragra.attendancesystem.entity.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CohortRepo extends JpaRepository<Cohort, Long> {

    List<Cohort> findCohortByName(String name);

}
