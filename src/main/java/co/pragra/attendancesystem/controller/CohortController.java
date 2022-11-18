package co.pragra.attendancesystem.controller;

import co.pragra.attendancesystem.dto.ErrorResponse;
import co.pragra.attendancesystem.entity.Cohort;
import co.pragra.attendancesystem.repo.CohortRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// LINE BELOW IS FOR UNIT TESTING PURPOSES ONLY.
@CrossOrigin(origins = "*")

@RestController
@Slf4j
@RequestMapping("/api")
public class CohortController {

    @Autowired
    private CohortRepo repo;

    public CohortController(CohortRepo repo) {
        this.repo = repo;
    }

    @GetMapping("/cohort")
    public ResponseEntity<?> getAllCohort(){
        List<Cohort> findAll = repo.findAll();
        if (findAll.size() >= 1) {
            return ResponseEntity.status(200).body(findAll);
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-C1")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message("No cohort found in database")
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }

    @GetMapping("/cohort/{id}")
    public ResponseEntity<?> findCohortById(@PathVariable long id){
        Optional<Cohort> cohortOptional = repo.findById(id);
        if (cohortOptional.isPresent()) {
            return ResponseEntity.status(200).body(cohortOptional.get());
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-C2")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Cohort with ID = [%s] not found in database", id))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }

    @GetMapping("/cohort/name/{name}")
    public ResponseEntity<?> findCohortByName(@PathVariable String name){
        List<Cohort> list = repo.findCohortByName(name);
        if (list.size() >= 1) {
            return ResponseEntity.status(200).body(list);
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-C3")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Cohort with name = [%s] not found in database", name))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }

    @PostMapping("/cohort")
    public ResponseEntity<?> createCohort(@RequestBody Cohort cohort){
        try {
            Cohort save = repo.save(cohort);
            return ResponseEntity.status(200).body(save);
        } catch (Exception e) {
            // LOG exception
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-C4")
                    .errorCode(500)
                    .dateTime(new Date())
                    .message(String.format("Cohort not created"))
                    .build();
            return ResponseEntity.status(500).body(errorR);
        }
    }

    @PutMapping("/cohort")
    public ResponseEntity<?> updateCohort(@RequestBody Cohort cohort){
        Optional<Cohort> byId = repo.findById(cohort.getId());
        if (byId.isPresent()) {
            try {
                Cohort save = repo.save(cohort);
                return ResponseEntity.status(200).body(save);
            } catch (Exception e) {
                // LOG exception
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-C5")
                        .errorCode(500)
                        .dateTime(new Date())
                        .message(String.format("Cohort not updated"))
                        .build();
                return ResponseEntity.status(500).body(errorR);
            }
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-C5")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Cohort with ID = [%s] not found in database", cohort.getId()))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }

    @DeleteMapping("/cohort/{id}")
    public ResponseEntity<?> deleteCohortById(@PathVariable long id){
        Optional<Cohort> byId = repo.findById(id);
        if (byId.isPresent()) {
            try {
                repo.deleteById(id);
                if (!repo.findById(id).isPresent()) {
                    return ResponseEntity.status(200).body(byId.get());
                } else {
                    ErrorResponse errorR = ErrorResponse.builder()
                            .appId("AMS-C6")
                            .errorCode(500)
                            .dateTime(new Date())
                            .message(String.format("Cohort not deleted"))
                            .build();
                    return ResponseEntity.status(500).body(errorR);
                }
            } catch (Exception e) {
                // LOG exception
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-C6")
                        .errorCode(500)
                        .dateTime(new Date())
                        .message(String.format("Cohort not deleted"))
                        .build();
                return ResponseEntity.status(500).body(errorR);
            }
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-C6")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Cohort with ID = [%s] not found in database", id))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }
}
