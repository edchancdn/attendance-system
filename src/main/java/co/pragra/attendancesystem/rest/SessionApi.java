package co.pragra.attendancesystem.rest;

import co.pragra.attendancesystem.entity.Session;
import co.pragra.attendancesystem.entity.Student;
import co.pragra.attendancesystem.repo.SessionRepo;
import co.pragra.attendancesystem.repo.StudentRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class SessionApi {

    private SessionRepo repo;
    private StudentRepo studentRepo;

    public SessionApi(SessionRepo repo, StudentRepo studentRepo) {
        this.repo = repo;
        this.studentRepo = studentRepo;
    }

    @GetMapping("/api/session")
    public List<Session> getAllSessions() {
        return repo.findAll();
    }

    @GetMapping("/api/session/{id}")
    public Optional<Session> getSessionById(@PathVariable Long id) {
        Optional<Session> byId = repo.findById(id);
        if (byId.isPresent()) {
            return byId;
        } else {
            return null;
        }
    }

    @PostMapping("/api/session")
    public Session createSession(@RequestBody Session session) {
        return repo.save(session);
    }

    @PutMapping("/api/session")
    public Session updateSession(@RequestBody Session session) {
        return repo.save(session);
    }

    @DeleteMapping("/api/session/{id}")
    public Session deleteSessionById(@PathVariable Long id) {
        Optional<Session> byId = repo.findById(id);
        if (byId.isPresent()) {
            Session rSession = new Session();
            rSession.setId(byId.get().getId());
            rSession.setSessionDate(byId.get().getSessionDate());
            rSession.setStartTime(byId.get().getStartTime());
            rSession.setEndTime(byId.get().getEndTime());
            rSession.setAttendedStudents(null);
            repo.deleteById(id);
            return rSession;
        } else {
            return null;
        }
    }

    /*
    Given a Session ID and a list of Student IDs,
    Add the corresponding students into existing session.attendedStudents.
    */
    @PutMapping("/api/session/{sessionId}/student")
    public Session addStudentToSession(@RequestBody List<Long> students, @PathVariable Long sessionId) {
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
                return repo.save(sessionToUpdate);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /*
    Given a Session ID and a list of Student IDs
    Remove the corresponding students from existing session.attendedStudents
     */
    @DeleteMapping("/api/session/{sessionId}/student")
    public Session deleteStudentFromSession(@RequestBody List<Long> students, @PathVariable Long sessionId) {
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
                return repo.save(sessionToUpdate);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
