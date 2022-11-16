package co.pragra.attendancesystem.controller;
import co.pragra.attendancesystem.entity.Session;
import co.pragra.attendancesystem.repo.SessionRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class SessionController {

    private SessionRepo sessionRepo;

    public SessionController(SessionRepo sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    @GetMapping("/session")
    public String getAllSessionList(Model model){
        model.addAttribute("session",sessionRepo.findAll());
        return "session";
    }

    @GetMapping ("/session/new")
    public String createNewSession(Model model){
        //creating empty session
        Session session = new Session();
        // then to add data into that by using form
        model.addAttribute("session",session);
        return "Create_Session";
    }

    //method for handling the data coming from post request inside Create_Student form
    @PostMapping("/session")
    public String savingSession(@ModelAttribute Session session){
        //now  our form is filled with data we get student in request back, but we still need to save inside database
        sessionRepo.save(session);
        //then returning the new view with created student
        return "redirect:/session";
    }


    // handling method if someone pressed update from the Students page(Main Page)
    @GetMapping("/session/edit/{id}")
    public String editSessionByID(@PathVariable("id") Long id, Model model){
        model.addAttribute("session",sessionRepo.findById(id).get());
        // take your student and go to edit page and modify it.
        return "edit_Session";
    }


    //handling post method after edit_Student
    @PostMapping("/session/edit/{id}")
    public String editSessionByID( @ModelAttribute Session session, Model model){
        //Now this method received updated student from the user, we accessing it through the @ModelAttribute. Then, saving it inside the database.
        sessionRepo.save(session);
        //returning to main page with updated student
        model.addAttribute("session",sessionRepo.findAll());
        return "redirect:/session";
    }


    //when someone pressed delete button on main page, request get deleted here and repo get updated. returning the main page view.
    @GetMapping("/session/delete/{id}")
    public String deleteSessionByID(@PathVariable("id") Long id){
        sessionRepo.deleteById(id);
        return "redirect:/session";
    }
}