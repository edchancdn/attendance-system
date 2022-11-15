package co.pragra.attendancesystem.rest;

import co.pragra.attendancesystem.entity.Cohort;
import co.pragra.attendancesystem.repo.CohortRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class CohortApi {

    @Autowired
    private CohortRepo repo;

    public CohortApi(CohortRepo repo) {
        this.repo = repo;
    }

    @PostMapping("api/cohort")
    public Cohort createCohort(@RequestBody Cohort cohort){
        return repo.save(cohort);
    }

    @GetMapping("/api/cohort/{id}")
    public Optional<Cohort> findCohortById(@PathVariable long id){
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

    @GetMapping("api/cohort")
    public List<Cohort> findAllCohort(){
        return repo.findAll();
    }

    @DeleteMapping("api/cohort/delete/{id}")
    public Cohort deleteCohortById(@PathVariable long id){
        try{
            Optional<Cohort> byId = repo.findById(id);
            if(byId.isPresent()){
                repo.deleteById(id);
                log.info("Cohort with Id: [{}] has been deleted successfully.", id);
                return byId.get();
            }
        }catch(NullPointerException e){
            e.getMessage();
            log.info("Couldn't find Cohort with Id: [{}] ", id);
            log.error("An error has occurred.");
        }
        return null;
    }

    @PutMapping("api/cohort")
    public Cohort updateCohort(@RequestBody Cohort cohort){
        return repo.save(cohort);
    }

    @GetMapping("api/cohort/name/{name}")
    public Cohort findCohortByName(@PathVariable String name){
        return repo.findCohortByName(name);
    }


}
