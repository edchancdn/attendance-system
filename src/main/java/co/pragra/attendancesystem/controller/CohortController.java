package co.pragra.attendancesystem.controller;

import co.pragra.attendancesystem.dto.ErrorResponse;
import co.pragra.attendancesystem.entity.Cohort;
import co.pragra.attendancesystem.entity.Session;
import co.pragra.attendancesystem.entity.Student;
import co.pragra.attendancesystem.repo.CohortRepo;
import co.pragra.attendancesystem.repo.StudentRepo;
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

    //@Autowired
    private CohortRepo repo;

    private StudentRepo studentRepo;

    public CohortController(CohortRepo repo, StudentRepo studentRepo) {
        this.repo = repo;
        this.studentRepo = studentRepo;
    }

    @GetMapping("/cohort")
    public ResponseEntity<?> getCohort(
            @RequestParam Optional<Long> id,
            @RequestParam Optional<String> name){
        if (id.isPresent()) {
            Optional<Cohort> cohortOptional = repo.findById(id.get());
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
        } else if (name.isPresent()) {
            List<Cohort> list = repo.findCohortByName(name.get());
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
        } else {
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
                    Cohort rCohort = new Cohort();
                    rCohort.setId(byId.get().getId());
                    rCohort.setName(byId.get().getName());
                    rCohort.setCourseName(byId.get().getCourseName());
                    rCohort.setStartDate(byId.get().getStartDate());
                    rCohort.setEndDate(byId.get().getEndDate());
                    rCohort.setStudents(null);
                    rCohort.setSessions(null);
                    return ResponseEntity.status(200).body(rCohort);
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

    /*
    Given a Cohort ID and a list of Student IDs,
    Add the corresponding students into existing cohort.students
    */
    @PutMapping("/cohort/{cohortId}/student")
    public ResponseEntity<?> addStudentToCohort(
            @RequestBody List<Long> students,
            @PathVariable Long cohortId) {
        // get the session to be updated
        Optional<Cohort> byId = repo.findById(cohortId);
        if (byId.isPresent()) {
            if (students.size() > 0) {
                Cohort cohortToUpdate = byId.get();
                // get the list to be updated
                List<Student> listToUpdate = cohortToUpdate.getStudents();
                for (Long sId: students) {
                    // get the student to be added to the list
                    Optional<Student> stById = studentRepo.findById(sId);
                    if (stById.isPresent()) {
                        Student studentAdd = stById.get();
                        // check if student does not exist in the list
                        if (!listToUpdate.contains(studentAdd)) {
                            listToUpdate.add(studentAdd);
                        }
                    }
                }
                cohortToUpdate.setStudents(listToUpdate);
                try {
                    Cohort save = repo.save(cohortToUpdate);
                    return ResponseEntity.status(200).body(save);
                } catch (Exception e) {
                    // LOG exception
                    ErrorResponse errorR = ErrorResponse.builder()
                            .appId("AMS-C7")
                            .errorCode(500)
                            .dateTime(new Date())
                            .message(String.format("Student not added to cohort"))
                            .build();
                    return ResponseEntity.status(500).body(errorR);
                }
            } else {
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-C7")
                        .errorCode(204)
                        .dateTime(new Date())
                        .message(String.format("Empty students list provided"))
                        .build();
                return ResponseEntity.status(204).body(errorR);
            }
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-C7")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Cohort with ID = [%s] not found in database", cohortId))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }

    /*
    Given a Cohort ID and a list of Student IDs
    Remove the corresponding students from existing cohort.students
     */
    @DeleteMapping("/cohort/{cohortId}/student")
    public ResponseEntity<?> deleteStudentFromCohort(
            @RequestBody List<Long> students,
            @PathVariable Long cohortId) {
        // get the cohort to be updated
        Optional<Cohort> byId = repo.findById(cohortId);
        if (byId.isPresent()) {
            if (students.size() > 0) {
                Cohort cohortToUpdate = byId.get();
                // get the list to be updated
                List<Student> listToUpdate = cohortToUpdate.getStudents();
                for (Long sId: students) {
                    // get the student to be removed from the list
                    Optional<Student> stById = studentRepo.findById(sId);
                    if (stById.isPresent()) {
                        Student studentRemove = stById.get();
                        // check if student exist in the list
                        if (listToUpdate.contains(studentRemove)) {
                            listToUpdate.remove(studentRemove);
                        }
                    }
                }
                cohortToUpdate.setStudents(listToUpdate);
                try {
                    Cohort save = repo.save(cohortToUpdate);
                    return ResponseEntity.status(200).body(save);
                } catch (Exception e) {
                    // LOG exception
                    ErrorResponse errorR = ErrorResponse.builder()
                            .appId("AMS-C8")
                            .errorCode(500)
                            .dateTime(new Date())
                            .message(String.format("Student not removed from cohort"))
                            .build();
                    return ResponseEntity.status(500).body(errorR);
                }
            } else {
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-C8")
                        .errorCode(204)
                        .dateTime(new Date())
                        .message(String.format("Empty students list provided"))
                        .build();
                return ResponseEntity.status(204).body(errorR);
            }
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-C8")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Cohort with ID = [%s] not found in database", cohortId))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }
}
