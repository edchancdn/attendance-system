package co.pragra.attendancesystem.controller;
import co.pragra.attendancesystem.entity.Cohort;
import co.pragra.attendancesystem.repo.CohortRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class CohortController {


    @Autowired
    private CohortRepo repo;

    public CohortController(CohortRepo repo) {
        this.repo = repo;
    }

    @GetMapping("/cohorts")
    public String findAllCohort(Model model){
        model.addAttribute("cohorts", repo.findAll());
        return "cohorts";
    }

    @GetMapping("/cohort/new")
    public String createNewCohort(Model model){
        model.addAttribute("cohort", new Cohort());
        return "Create_cohort";
    }

    @PostMapping("cohorts")
    public String savingCohort(@ModelAttribute Cohort cohort){
        repo.save(cohort);
        return "redirect:/cohorts";
    }


    @GetMapping("/cohorts/edit/{id}")
    public String editCohortById(@PathVariable long id, Model model){
        model.addAttribute("cohort", repo.findById(id).get());
        return "Edit_Cohort";
    }

    @PostMapping("cohorts/edit/{id}")
    public String editCohortById(@ModelAttribute Cohort cohort, Model model){
        repo.save(cohort);
        model.addAttribute("cohorts", repo.findAll());
        return "redirect:/cohorts";
    }


    @GetMapping("/cohorts/delete/{id}")
    public String deleteCohortById(@PathVariable long id){
        repo.deleteById(id);
        return "redirect:/cohorts";
    }
}
