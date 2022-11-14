package co.pragra.attendancesystem.rest;

import co.pragra.attendancesystem.entity.Student;
import co.pragra.attendancesystem.repo.StudentRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class StudentApi {
    private StudentRepo repo;

    public StudentApi(StudentRepo repo) {
        this.repo = repo;
    }

    @GetMapping("/api/student")
    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    @GetMapping("/api/student/{id}")
    public Optional<Student> getStudentById(@PathVariable Long id) {
        return repo.findById(id);
    }

    @GetMapping("/api/student/lastname/{lastname}")
    public List<Student> getStudentByLastName(@PathVariable String lastname) {
        return repo.searchStudentByLastName(lastname);
    }

    @PostMapping("api/student")
    public Student createStudent(@RequestBody Student student) {
        return repo.save(student);
    }

    @PutMapping("api/student")
    public Student updateStudent(@RequestBody Student student) {
        return repo.save(student);
    }

    @DeleteMapping("/api/student/{id}")
    public Student deleteStudentById(@PathVariable Long id) {
        Optional<Student> byId = repo.findById(id);
        if (byId.isPresent()) {
            repo.deleteById(id);
        }
        return byId.get();
    }

}
