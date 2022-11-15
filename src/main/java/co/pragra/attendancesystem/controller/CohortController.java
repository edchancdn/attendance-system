package co.pragra.attendancesystem.controller;
import co.pragra.attendancesystem.entity.Cohort;
import co.pragra.attendancesystem.repo.CohortRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class CohortController {
    private CohortRepo cohortRepo;

    public CohortController(CohortRepo cohortRepo) {
        this.cohortRepo = cohortRepo;
    }

    @GetMapping("/cohorts")
    public String getAllCohort(Model model){
        model.addAttribute("cohorts",cohortRepo.findAll());
        return "cohorts";
    }

    @GetMapping("/cohort/new")
    public String addNewCohort(Model model){
        Cohort cohort = new Cohort();
        model.addAttribute("cohort",cohort);
        return "cohort_create";
    }

    //handing request coming in from adding cohort
    @PostMapping("/cohorts")
    public String newCohortAdded(@ModelAttribute Cohort cohort, Model model){
        //updating repo
        cohortRepo.save(cohort);
        //return to main page
        model.addAttribute("cohorts",cohortRepo.findAll());
        return "redirect:/cohorts";
    }

    @GetMapping("/cohort/edit/{id}")
    public String updatingCohort(@PathVariable("id") Long id, Model model){

        model.addAttribute("cohort",cohortRepo.findById(id).get());
        //return to updating page
        return "edit_Cohort";
    }

    @PostMapping("/cohort/edit/{id}")
    public String editCohort(@ModelAttribute Cohort cohort){

        cohortRepo.save(cohort);
        //return to main page
        return "redirect:/cohorts";
    }

    @GetMapping("/cohort/delete/{id}")
    public String deletingCohort(@PathVariable("id") Long id){
        cohortRepo.deleteById(id);
        //return to main page
        return "redirect:/cohorts";
    }
}

