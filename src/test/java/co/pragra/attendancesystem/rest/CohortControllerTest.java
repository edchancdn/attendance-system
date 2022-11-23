package co.pragra.attendancesystem.rest;

import co.pragra.attendancesystem.controller.CohortController;
import co.pragra.attendancesystem.entity.Cohort;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CohortControllerTest {

    @Autowired
    private CohortController api;
    private Cohort cohort1;

    @BeforeAll
    public void setUp() {
        System.out.println("Unit test started for StudentController.");
        System.out.println("Running tests...");
    }

    @Test
    @Order(1)
    void testCreateCohort() {
        String name = "Jan-2021";
        Cohort cohort = new Cohort().builder()
                .name(name)
                .courseName("Front End Programming")
                .startDate(new Date())
                .endDate(new Date()).build();
        ResponseEntity<?> createdCohort = api.createCohort(cohort);
        Assertions.assertTrue(createdCohort.getStatusCodeValue() == 200);
        cohort1 = (Cohort) createdCohort.getBody();
        Assertions.assertTrue(cohort1.getName().equals(name));
    }

    @Test
    @Order(2)
    void findCohortById() {
        ResponseEntity<?> cohortId = api.getCohort(Optional.ofNullable(cohort1.getId()), Optional.empty());
        Assertions.assertTrue(cohortId.getStatusCodeValue() == 200);
        Cohort responseBody = (Cohort) cohortId.getBody();
        Assertions.assertTrue(responseBody.getId() == cohort1.getId());
    }

    @Test
    @Order(3)
    void findCohortByName() {
        ResponseEntity<?> cohortName = api.getCohort(Optional.empty(), Optional.ofNullable(cohort1.getName()));
        Assertions.assertTrue(cohortName.getStatusCodeValue() == 200);
        List<Cohort> responseBody = (List<Cohort>) cohortName.getBody();
        Assertions.assertTrue(responseBody.get(0).getName().equals(cohort1.getName()));
    }

    @Test
    @Order(4)
    void findAllCohort() {
        ResponseEntity<?> allCohorts = api.getCohort(Optional.empty(), Optional.empty());
        Assertions.assertTrue(allCohorts.getStatusCodeValue() == 200);
    }

    @Test
    @Order(5)
    void updateCohort() {
        String name = "July-2022";
        Cohort cohort = new Cohort().builder()
                .id(cohort1.getId())
                .name(name)
                .startDate(cohort1.getStartDate())
                .endDate(cohort1.getEndDate())
                .courseName(cohort1.getCourseName()).build();
        ResponseEntity<?> updateCohort = api.updateCohort(cohort);
        Assertions.assertTrue(updateCohort.getStatusCodeValue() == 200);
        Cohort responseBody = (Cohort) updateCohort.getBody();
        Assertions.assertTrue(responseBody.getName().equals(name));
    }

    @Test
    @Order(6)
    void deleteCohortById() {
        ResponseEntity<?> deleteCohortById = api.deleteCohortById(cohort1.getId());
        Assertions.assertTrue(deleteCohortById.getStatusCodeValue() == 200);
        ResponseEntity<?> cohortById = api.getCohort(Optional.ofNullable(cohort1.getId()), Optional.empty());
        Assertions.assertTrue(cohortById.getStatusCodeValue() == 404);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Tests completed.");
        System.out.println("Unit test ended for SessionController.");
    }
}
