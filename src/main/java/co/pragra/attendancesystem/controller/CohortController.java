package co.pragra.attendancesystem.controller;

import co.pragra.attendancesystem.entity.Cohort;
import co.pragra.attendancesystem.service.CohortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CohortController {

    @Autowired
    private CohortService service;

    @PostMapping("api/cohort")
    public Cohort createCohort(@RequestBody Cohort cohort){
        return service.createCohort(cohort);
    }

    @GetMapping("/api/cohort/{id}")
    public Optional<Cohort> findCohortById(@PathVariable long id){
        return service.findCohortById(id);
    }

    @GetMapping("api/cohort")
    public List<Cohort> findAllCohort(){
        return service.findAllCohort();
    }

    @DeleteMapping("api/cohort/delete/{id}")
    public void deleteCohortById(@PathVariable long id){
        service.deleteCohortById(id);
    }

}
