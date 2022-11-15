package co.pragra.attendancesystem.rest;

import co.pragra.attendancesystem.entity.Session;
import co.pragra.attendancesystem.entity.Student;
import co.pragra.attendancesystem.repo.SessionRepo;
import co.pragra.attendancesystem.repo.StudentRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        Assertions.assertTrue(api.getAllSessions().size() >= 1);
    }

    @Test
    @Order(2)
    public void testGetSessionById() {
        Assertions.assertTrue(api.getSessionById(tSession.getId()).isPresent());
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
        Session updatedSession = api.updateSession(session);
        Assertions.assertTrue(updatedSession.getSessionDate().equals(T_UPDATED_DATE));
    }

    @Test
    @Order(4)
    public void testAddStudentToSession() {
        Optional<Student> byId = studentRepo.findById(studentId3);
        List<Long> studentIds = new ArrayList<>();
        studentIds.add(studentId3);
        Session updatedSession = api.addStudentToSession(studentIds, tSession.getId());
        Assertions.assertTrue(updatedSession.getAttendedStudents().contains(byId.get()));
    }

    @Test
    @Order(5)
    public void testDeleteStudentToSession() {
        Optional<Student> byId = studentRepo.findById(studentId3);
        List<Long> studentIds = new ArrayList<>();
        studentIds.add(studentId3);
        Session updatedSession = api.deleteStudentFromSession(studentIds, tSession.getId());
        Assertions.assertFalse(updatedSession.getAttendedStudents().contains(byId.get()));
    }

    @Test
    @Order(6)
    public void testDeleteSessionById() {
        api.deleteSessionById(tSession.getId());
        Assertions.assertEquals(null, api.getSessionById(tSession.getId()));
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
