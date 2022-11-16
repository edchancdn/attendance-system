package co.pragra.attendancesystem.rest;

import co.pragra.attendancesystem.entity.Session;
import co.pragra.attendancesystem.entity.Student;
import co.pragra.attendancesystem.repo.SessionRepo;
import co.pragra.attendancesystem.repo.StudentRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.sql.Time;
import java.util.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SessionApiTest {

    @Autowired
    private SessionRepo repo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private SessionApi api;
    private Session tSession = new Session();
    private Long studentId1;
    private Long studentId2;
    private Long studentId3;
    private static final Date T_UPDATED_DATE = new GregorianCalendar(2022, Calendar.JULY, 3).getTime();

    @BeforeAll
    public void setUp() {
        System.out.println("Unit test started for SessionApi.");
        System.out.println("Populating test data...");
        Student newSt1 = studentRepo.save(new Student().builder()
                .firstName("Jane")
                .lastName("Doe")
                .build());
        studentId1 = newSt1.getId();
        Student newSt2 = studentRepo.save(new Student().builder()
                .firstName("John")
                .lastName("Smith")
                .build());
        studentId2 = newSt1.getId();
        Student newSt3 = studentRepo.save(new Student().builder()
                .firstName("William")
                .lastName("Johnson")
                .build());
        studentId3 = newSt3.getId();
        List<Student> students = new ArrayList<>();
        students.add(newSt1);
        students.add(newSt2);
        Session newS = new Session().builder()
                .sessionDate(new GregorianCalendar(2022, Calendar.JULY, 1).getTime())
                .startTime(Time.valueOf("09:30:00"))
                .endTime(Time.valueOf("12:30:00"))
                .attendedStudents(students)
                .build();
        tSession = repo.save(newS);
        System.out.println("Test data populated.");
        System.out.println("Running tests...");
    }

    @Test
    @Order(1)
    public void testGetAllSessions() {
        ResponseEntity<?> allStudents = api.getAllSessions();
        Assertions.assertTrue(allStudents.getStatusCodeValue() == 200);
    }

    @Test
    @Order(2)
    public void testGetSessionById() {
        ResponseEntity<?> sessionById = api.getSessionById(tSession.getId());
        Assertions.assertTrue(sessionById.getStatusCodeValue() == 200);
        Session responseBody = (Session) sessionById.getBody();
        Assertions.assertTrue(responseBody.getId() == tSession.getId());
    }

    @Test
    @Order(3)
    public void testUpdateSession() {
        Session session = new Session().builder()
                .id(tSession.getId())
                .sessionDate(T_UPDATED_DATE)
                .startTime(tSession.getStartTime())
                .endTime(tSession.getEndTime())
                .attendedStudents(tSession.getAttendedStudents())
                .build();
        ResponseEntity<?> updatedSession = api.updateSession(session);
        Assertions.assertTrue(updatedSession.getStatusCodeValue() == 200);
        Session responseBody = (Session) updatedSession.getBody();
        Assertions.assertTrue(responseBody.getSessionDate().equals(T_UPDATED_DATE));
    }

    @Test
    @Order(4)
    public void testAddStudentToSession() {
        Optional<Student> byId = studentRepo.findById(studentId3);
        List<Long> studentIds = new ArrayList<>();
        studentIds.add(studentId3);
        ResponseEntity<?> updatedSession = api.addStudentToSession(studentIds, tSession.getId());
        Assertions.assertTrue(updatedSession.getStatusCodeValue() == 200);
        Session responseBody = (Session) updatedSession.getBody();
        Assertions.assertTrue(responseBody.getAttendedStudents().contains(byId.get()));
    }

    @Test
    @Order(5)
    public void testDeleteStudentToSession() {
        Optional<Student> byId = studentRepo.findById(studentId3);
        List<Long> studentIds = new ArrayList<>();
        studentIds.add(studentId3);
        // When running the unit test - use (fetch = FetchType.EAGER) in Session entity
        ResponseEntity<?> updatedSession = api.deleteStudentFromSession(studentIds, tSession.getId());
        Assertions.assertTrue(updatedSession.getStatusCodeValue() == 200);
        Session responseBody = (Session) updatedSession.getBody();
        Assertions.assertFalse(responseBody.getAttendedStudents().contains(byId.get()));
    }

    @Test
    @Order(6)
    public void testDeleteSessionById() {
        // When running the unit test - use (fetch = FetchType.EAGER) in Session entity
        ResponseEntity<?> deleteSessionById = api.deleteSessionById(tSession.getId());
        Assertions.assertTrue(deleteSessionById.getStatusCodeValue() == 200);
        ResponseEntity<?> sessionById = api.getSessionById(tSession.getId());
        Assertions.assertTrue(sessionById.getStatusCodeValue() == 404);
    }

    @AfterAll
    public void tearDown() {
        System.out.println("Tests completed.");
        System.out.println("Clearing test data...");
        if (repo.findById(tSession.getId()).isPresent()) {
            repo.deleteById(tSession.getId());
        }
        if (studentRepo.findById(studentId1).isPresent()) {
            studentRepo.deleteById(studentId1);
        }
        if (studentRepo.findById(studentId2).isPresent()) {
            studentRepo.deleteById(studentId2);
        }
        if (studentRepo.findById(studentId3).isPresent()) {
            studentRepo.deleteById(studentId3);
        }
        System.out.println("Test data cleared.");
        System.out.println("Unit test ended for SessionApi.");
    }
}
