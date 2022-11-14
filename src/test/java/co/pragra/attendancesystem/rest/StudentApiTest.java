package co.pragra.attendancesystem.rest;

import co.pragra.attendancesystem.entity.Student;
import co.pragra.attendancesystem.repo.StudentRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentApiTest {

    @Autowired
    private StudentRepo repo;
    @Autowired
    private StudentApi api;
    private List<Student> tData = new ArrayList<>();
    private Student tStudent = new Student();
    // Constant test data
    private static final String T_LASTNAME = "Smithfortesting";
    private static final String T_FIRSTNAME = "John";
    private static final String U_FIRSTNAME = "Frank";

    @BeforeAll
    public void setUp() {
        Student newSt = new Student().builder()
                .lastName(T_LASTNAME)
                .firstName(T_FIRSTNAME).build();
        tStudent = repo.save(newSt);
        tData = repo.findAll();
    }

    @Test
    @Order(1)
    public void testGetAllStudents() {
        Assertions.assertTrue(api.getAllStudents().size() >= 1);
    }

    @Test
    @Order(2)
    public void testGetStudentById() {
        Assertions.assertTrue(api.getStudentById(tStudent.getId()).isPresent());
    }

    @Test
    @Order(3)
    public void testGetStudentByLastName() {
        Assertions.assertTrue(api.getStudentByLastName(T_LASTNAME).size() >= 1);
    }

    @Test
    @Order(4)
    public void testUpdateStudent() {
        Student student = new Student().builder()
                .id(tStudent.getId())
                .lastName(tStudent.getLastName())
                .firstName(U_FIRSTNAME).build();
        Student updatedStudent = api.updateStudent(student);
        Assertions.assertTrue(updatedStudent.getFirstName() == U_FIRSTNAME);
    }

    @Test
    @Order(5)
    public void testDeleteStudentById() {
        api.deleteStudentById(tStudent.getId());
        Assertions.assertFalse(api.getStudentById(tStudent.getId()).isPresent());
    }

    @AfterAll
    public void tearDown() {}
}
