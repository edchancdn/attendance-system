package co.pragra.attendancesystem.repo;

import co.pragra.attendancesystem.entity.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CohortRepo extends JpaRepository<Cohort, Long> {

    Cohort findCohortByName(String name);

}
