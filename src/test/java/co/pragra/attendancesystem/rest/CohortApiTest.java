package co.pragra.attendancesystem.rest;

import co.pragra.attendancesystem.entity.Cohort;
import co.pragra.attendancesystem.repo.CohortRepo;
import co.pragra.attendancesystem.rest.CohortApi;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CohortApiTest {

    @Autowired
    private  CohortRepo repo;

    @Autowired
    private CohortApi controller;


    private Cohort cohort1;

    private List<Cohort> cohorts;


    @BeforeAll
    public void setUp() {
        Cohort cohort = Cohort.builder()
                .name("April-2022")
                .courseName("Java FullStack")
                .startDate(new Date())
                .endDate(new Date()).build();

        cohort1 = repo.save(cohort);
        cohorts = repo.findAll();
        System.out.println(cohorts.size());
        System.out.println(cohort1.getId());
    }



    @Test
    @Order(1)
    void testCreateCohort() {
        controller.createCohort(new Cohort().builder().name("Jan-2021").courseName("Front End Programming").startDate(new Date()).endDate(new Date()).build());
       Assertions.assertTrue(controller.findAllCohort().size()>1);
    }

    @Test
    @Order(2)
    void findCohortById() {
        Assertions.assertTrue(controller.findCohortById(1).isPresent());
    }

    @Test
    @Order(3)
    void findAllCohort() {
        repo.save(Cohort.builder().name("January-2022").courseName("DevOps").startDate(new Date()).endDate(new Date()).build());
        Assertions.assertTrue(controller.findAllCohort().size() >=2);
    }

    @Test
    @Order(4)
    void updateCohort() {
        Cohort cohort2 = controller.updateCohort(new Cohort().builder().name("July-2022").startDate(new Date())
                .endDate(new Date()).courseName(cohort1.getCourseName()).build());
        Assertions.assertTrue(cohort2.getName().equals("July-2022"));
    }

    @Test
    @Order(5)
    void findCohortByName() {
        Assertions.assertTrue(controller.findCohortByName("April-2022").getName().equals("April-2022"));
    }

    @Test
    @Order(6)
    void deleteCohortById() {
        controller.deleteCohortById(1);
        Assertions.assertFalse(controller.findCohortById(1).isPresent());
    }

    @AfterEach
    void tearDown() {

    }
}
