package co.pragra.attendancesystem.controller;

import co.pragra.attendancesystem.dto.ErrorResponse;
import co.pragra.attendancesystem.entity.Session;
import co.pragra.attendancesystem.entity.Student;
import co.pragra.attendancesystem.repo.SessionRepo;
import co.pragra.attendancesystem.repo.StudentRepo;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// LINE BELOW IS FOR UNIT TESTING PURPOSES ONLY.
@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api")
public class SessionController {

    private SessionRepo repo;
    private StudentRepo studentRepo;

    public SessionController(SessionRepo repo, StudentRepo studentRepo) {
        this.repo = repo;
        this.studentRepo = studentRepo;
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSession(
            @RequestParam Optional<Long> id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<Date> sessionDate) {

        // ID is prioritized
        // If both ID and session have values, then only search by ID
        if (id.isPresent()) {
            Optional<Session> sessionOptional = repo.findById(id.get());
            if (sessionOptional.isPresent()) {
                return ResponseEntity.status(200).body(sessionOptional.get());
            } else {
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-X2")
                        .errorCode(404)
                        .dateTime(new Date())
                        .message(String.format("Session with ID = [%s] not found in database", id))
                        .build();
                return ResponseEntity.status(404).body(errorR);
            }

            // Session date is secondary
        } else if (sessionDate.isPresent()) {
            Date date = sessionDate.get();
            System.out.println(date);
            List<Session> findBySessionDate = repo.searchSessionBySessionDate(sessionDate.get());
            if (findBySessionDate.size() >= 1) {
                return ResponseEntity.status(200).body(findBySessionDate);
            } else {
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-X1")
                        .errorCode(404)
                        .dateTime(new Date())
                        .message(String.format("Sessions with session date = [%s] not found in database", sessionDate.get()))
                        .build();
                return ResponseEntity.status(404).body(errorR);
            }

            // Fallback to search all
        } else {
            List<Session> findAll = repo.findAll();
            if (findAll.size() >= 1) {
                return ResponseEntity.status(200).body(findAll);
            } else {
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-X1")
                        .errorCode(404)
                        .dateTime(new Date())
                        .message("No session found in database")
                        .build();
                return ResponseEntity.status(404).body(errorR);
            }
        }
    }

    @PostMapping("/session")
    public ResponseEntity<?> createSession(@RequestBody Session session) {
        try {
            Session save = repo.save(session);
            return ResponseEntity.status(200).body(save);
        } catch (Exception e) {
            // LOG exception
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-X4")
                    .errorCode(500)
                    .dateTime(new Date())
                    .message(String.format("Session not created"))
                    .build();
            return ResponseEntity.status(500).body(errorR);
        }
    }

    @PutMapping("/session")
    public ResponseEntity<?> updateSession(@RequestBody Session session) {
        Optional<Session> byId = repo.findById(session.getId());
        if (byId.isPresent()) {
            try {
                Session save = repo.save(session);
                return ResponseEntity.status(200).body(save);
            } catch (Exception e) {
                // LOG exception
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-X4")
                        .errorCode(500)
                        .dateTime(new Date())
                        .message(String.format("Session not updated"))
                        .build();
                return ResponseEntity.status(500).body(errorR);
            }
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-X4")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Session with ID = [%s] not found in database", session.getId()))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }

    @DeleteMapping("/session/{id}")
    public ResponseEntity<?> deleteSessionById(@PathVariable Long id) {
        Optional<Session> byId = repo.findById(id);
        if (byId.isPresent()) {
            try {
                repo.deleteById(id);
                if (!repo.findById(id).isPresent()) {
                    Session rSession = new Session();
                    rSession.setId(byId.get().getId());
                    rSession.setSessionDate(byId.get().getSessionDate());
                    rSession.setStartTime(byId.get().getStartTime());
                    rSession.setEndTime(byId.get().getEndTime());
                    rSession.setAttendedStudents(null);
                    return ResponseEntity.status(200).body(rSession);
                } else {
                    ErrorResponse errorR = ErrorResponse.builder()
                            .appId("AMS-X5")
                            .errorCode(500)
                            .dateTime(new Date())
                            .message(String.format("System exception encountered when trying to delete this session."))
                            .build();
                    return ResponseEntity.status(500).body(errorR);
                }
            } catch (Exception e) {
                // Log exception
                String errMsg = "System exception encountered when trying to delete this session.";
                if (e.getMessage().contains("org.hibernate.exception.ConstraintViolationException:")) {
                    errMsg = "This session is referenced by a cohort. " +
                            "Remove references from any cohort, before deleting this session.";
                }
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-X5")
                        .errorCode(500)
                        .dateTime(new Date())
                        .message(errMsg)
                        .build();
                return ResponseEntity.status(500).body(errorR);
            }
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-X5")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Session with ID = [%s] not found in database", id))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }

    /*
    Given a Session ID and a list of Student IDs,
    Add the corresponding students into existing session.attendedStudents.
    */
    @PutMapping("/session/{sessionId}/student")
    public ResponseEntity<?> addStudentToSession(@RequestBody List<Long> students, @PathVariable Long sessionId) {
        // get the session to be updated
        Optional<Session> byId = repo.findById(sessionId);
        if (byId.isPresent()) {
            if (students.size() > 0) {
                Session sessionToUpdate = byId.get();
                // get the list to be updated
                List<Student> listToUpdate = sessionToUpdate.getAttendedStudents();
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
                sessionToUpdate.setAttendedStudents(listToUpdate);
                try {
                    Session save = repo.save(sessionToUpdate);
                    return ResponseEntity.status(200).body(save);
                } catch (Exception e) {
                    // LOG exception
                    ErrorResponse errorR = ErrorResponse.builder()
                            .appId("AMS-X6")
                            .errorCode(500)
                            .dateTime(new Date())
                            .message(String.format("Student not added to session"))
                            .build();
                    return ResponseEntity.status(500).body(errorR);
                }
            } else {
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-X6")
                        .errorCode(204)
                        .dateTime(new Date())
                        .message(String.format("Empty students list provided"))
                        .build();
                return ResponseEntity.status(204).body(errorR);
            }
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-X6")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Session with ID = [%s] not found in database", sessionId))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }

    /*
    Given a Session ID and a list of Student IDs
    Remove the corresponding students from existing session.attendedStudents
     */
    @DeleteMapping("/session/{sessionId}/student")
    public ResponseEntity<?> deleteStudentFromSession(@RequestBody List<Long> students, @PathVariable Long sessionId) {
        // get the session to be updated
        Optional<Session> byId = repo.findById(sessionId);
        if (byId.isPresent()) {
            if (students.size() > 0) {
                Session sessionToUpdate = byId.get();
                // get the list to be updated
                List<Student> listToUpdate = sessionToUpdate.getAttendedStudents();
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
                sessionToUpdate.setAttendedStudents(listToUpdate);
                try {
                    Session save = repo.save(sessionToUpdate);
                    return ResponseEntity.status(200).body(save);
                } catch (Exception e) {
                    // LOG exception
                    ErrorResponse errorR = ErrorResponse.builder()
                            .appId("AMS-X7")
                            .errorCode(500)
                            .dateTime(new Date())
                            .message(String.format("Student not removed from session"))
                            .build();
                    return ResponseEntity.status(500).body(errorR);
                }
            } else {
                ErrorResponse errorR = ErrorResponse.builder()
                        .appId("AMS-X7")
                        .errorCode(204)
                        .dateTime(new Date())
                        .message(String.format("Empty students list provided"))
                        .build();
                return ResponseEntity.status(204).body(errorR);
            }
        } else {
            ErrorResponse errorR = ErrorResponse.builder()
                    .appId("AMS-X7")
                    .errorCode(404)
                    .dateTime(new Date())
                    .message(String.format("Session with ID = [%s] not found in database", sessionId))
                    .build();
            return ResponseEntity.status(404).body(errorR);
        }
    }
}
