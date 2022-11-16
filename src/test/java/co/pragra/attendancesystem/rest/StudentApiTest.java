package co.pragra.attendancesystem.rest;

import co.pragra.attendancesystem.entity.Student;
import co.pragra.attendancesystem.repo.StudentRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentApiTest {

    @Autowired
    private StudentApi api;
    private Student tStudent = new Student();
    private static final String T_LASTNAME = "Smithfortesting";
    private static final String U_FIRSTNAME = "Frank";

    @BeforeAll
    public void setUp() {
        System.out.println("Unit test started for StudentApi.");
        System.out.println("Running tests...");
    }

    @Test
    @Order(1)
    public void testCreateStudent() {
        Student student = new Student().builder()
                .lastName(T_LASTNAME)
                .firstName("John").build();
        ResponseEntity<?> createdStudent = api.createStudent(student);
        Assertions.assertTrue(createdStudent.getStatusCodeValue() == 200);
        tStudent = (Student) createdStudent.getBody();
        Assertions.assertTrue(tStudent.getLastName().equals(T_LASTNAME));
    }

    @Test
    @Order(2)
    public void testGetAllStudents() {
        ResponseEntity<?> allStudents = api.getAllStudents();
        Assertions.assertTrue(allStudents.getStatusCodeValue() == 200);
    }

    @Test
    @Order(3)
    public void testGetStudentById() {
        ResponseEntity<?> studentById = api.getStudentById(tStudent.getId());
        Assertions.assertTrue(studentById.getStatusCodeValue() == 200);
        Student responseBody = (Student) studentById.getBody();
        Assertions.assertTrue(responseBody.getId() == tStudent.getId());
    }

    @Test
    @Order(4)
    public void testGetStudentByLastName() {
        ResponseEntity<?> studentByLastName = api.getStudentByLastName(T_LASTNAME);
        Assertions.assertTrue(studentByLastName.getStatusCodeValue() == 200);
        List<Student> responseBody = (List<Student>) studentByLastName.getBody();
        Assertions.assertTrue(responseBody.get(0).getLastName().equals(T_LASTNAME));
    }

    @Test
    @Order(5)
    public void testUpdateStudent() {
        Student student = new Student().builder()
                .id(tStudent.getId())
                .lastName(tStudent.getLastName())
                .firstName(U_FIRSTNAME).build();
        ResponseEntity<?> updatedStudent = api.updateStudent(student);
        Assertions.assertTrue(updatedStudent.getStatusCodeValue() == 200);
        Student responseBody = (Student) updatedStudent.getBody();
        Assertions.assertTrue(responseBody.getFirstName().equals(U_FIRSTNAME));
    }

    @Test
    @Order(6)
    public void testDeleteStudentById() {
        ResponseEntity<?> deleteStudentById = api.deleteStudentById(tStudent.getId());
        Assertions.assertTrue(deleteStudentById.getStatusCodeValue() == 200);
        ResponseEntity<?> studentById = api.getStudentById(tStudent.getId());
        Assertions.assertTrue(studentById.getStatusCodeValue() == 404);
    }

    @AfterAll
    public void tearDown() {
        System.out.println("Tests completed.");
        System.out.println("Unit test ended for SessionApi.");
    }
}
