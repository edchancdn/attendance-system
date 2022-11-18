package co.pragra.attendancesystem.controller;

import co.pragra.attendancesystem.dto.ErrorResponse;
import co.pragra.attendancesystem.entity.Student;
import co.pragra.attendancesystem.repo.StudentRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// LINE BELOW IS FOR UNIT TESTING PURPOSES ONLY.
@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api")
public class StudentController {
    private StudentRepo repo;

    public StudentController(StudentRepo repo) {
        this.repo = repo;
    }

    @GetMapping("/student")
    public ResponseEntity<?> getStudent(
            @RequestParam Optional<Long> id,
            @RequestParam Optional<String> lastname) {

        // ID is prioritized
        // If both ID and Lastname have values, then only search by ID
        if (id.isPresent()) {
            Optional<Student> studentOptional = repo.findById(id.get());
            if (studentOptional.isPresent()) {
                return ResponseEntity.status(200).body(studentOptional.get());
            } else {
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-S2")
                        .errorCode(404)
                        .dateTime(new Date())
                        .message(String.format("Student with ID = [%s] not found in database", id))
                        .build();
                return ResponseEntity.status(404).body(errorR);
            }

            // Lastname is secondary
        } else if (lastname.isPresent()) {
            List<Student> list = repo.searchStudentByLastName(lastname.get());
            if (list.size() >= 1) {
                return ResponseEntity.status(200).body(list);
            } else {
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-S3")
                        .errorCode(404)
                        .dateTime(new Date())
                        .message(String.format("Student with lastname = [%s] not found in database", lastname.get()))
                        .build();
                return ResponseEntity.status(404).body(errorR);
            }

            // Fallback to search all
        } else {
            List<Student> findAll = repo.findAll();
            if (findAll.size() >= 1) {
                return ResponseEntity.status(200).body(findAll);
            } else {
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-S1")
                        .errorCode(404)
                        .dateTime(new Date())
                        .message("No student found in database")
                        .build();
                return ResponseEntity.status(404).body(errorR);
            }
        }
    }

    @PostMapping("/student")
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        try {
            Student save = repo.save(student);
            return ResponseEntity.status(200).body(save);
        } catch (Exception e) {
            // LOG exception
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-S4")
                    .errorCode(500)
                    .dateTime(new Date())
                    .message(String.format("Student not created"))
                    .build();
            return ResponseEntity.status(500).body(errorR);
        }
    }

    @PutMapping("/student")
    public ResponseEntity<?> updateStudent(@RequestBody Student student) {
        Optional<Student> byId = repo.findById(student.getId());
        if (byId.isPresent()) {
            try {
                Student save = repo.save(student);
                return ResponseEntity.status(200).body(save);
            } catch (Exception e) {
                // LOG exception
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-S5")
                        .errorCode(500)
                        .dateTime(new Date())
                        .message(String.format("Student not updated"))
                        .build();
                return ResponseEntity.status(500).body(errorR);
            }
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-S5")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Student with ID = [%s] not found in database", student.getId()))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }

    @DeleteMapping("/student/{id}")
    public ResponseEntity<?> deleteStudentById(@PathVariable Long id) {
        Optional<Student> byId = repo.findById(id);
        if (byId.isPresent()) {
            try {
                repo.deleteById(id);
                if (!repo.findById(id).isPresent()) {
                    return ResponseEntity.status(200).body(byId.get());
                } else {
                    ErrorResponse errorR = ErrorResponse.builder()
                            .appId("AMS-S6")
                            .errorCode(500)
                            .dateTime(new Date())
                            .message(String.format("Student not deleted"))
                            .build();
                    return ResponseEntity.status(500).body(errorR);
                }
            } catch (Exception e) {
                // LOG exception
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-S6")
                        .errorCode(500)
                        .dateTime(new Date())
                        .message(String.format("Student not deleted"))
                        .build();
                return ResponseEntity.status(500).body(errorR);
            }
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-S6")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Student with ID = [%s] not found in database", id))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }

}
