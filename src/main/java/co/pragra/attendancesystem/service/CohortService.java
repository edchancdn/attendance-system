package co.pragra.attendancesystem.service;

import co.pragra.attendancesystem.entity.Cohort;
import co.pragra.attendancesystem.repo.CohortRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CohortService {

    @Autowired
    private final CohortRepo repo;

    public CohortService(CohortRepo repo) {
        this.repo = repo;
    }


    public Cohort createCohort(Cohort cohort){
        return repo.save(cohort);
    }

    public Optional<Cohort> findCohortById(long id){
        try{
            Optional<Cohort> byId = repo.findById(id);
            return byId;
        }catch(NullPointerException e){
            e.getMessage();
            log.info("Couldn't find Cohort with Id: [{}] ", id);
            log.error("An error has occurred.");
        }
        return null;
    }

    public List<Cohort> findAllCohort(){
        return repo.findAll();
    }

    public void deleteCohortById(long id){
        try{
            Optional<Cohort> byId = repo.findById(id);
           if(byId.isPresent()){
               repo.deleteById(id);
               log.info("Cohort with Id: [{}] has been deleted successfully.", id);
           }
        }catch(NullPointerException e){
            e.getMessage();
            log.info("Couldn't find Cohort with Id: [{}] ", id);
            log.error("An error has occurred.");
        }

    }
}
