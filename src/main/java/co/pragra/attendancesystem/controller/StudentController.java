package co.pragra.attendancesystem.controller;
import co.pragra.attendancesystem.entity.Student;
import co.pragra.attendancesystem.repo.StudentRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class StudentController {

    private StudentRepo studentRepo;

    public StudentController(StudentRepo studentRepo) {
        this.studentRepo = studentRepo;
    }

    @GetMapping("/students")
    public String getAllStudentList(Model model){
        model.addAttribute("students",studentRepo.findAll());
        return "students";
    }

    @GetMapping ("/students/new")
    public String creatNewStudent(Model model){
        //creating empty student
        Student student = new Student();
        // then to add data into that by using form
        model.addAttribute("student",student);
        return "Create_Student";
    }

    //method for handling the data coming from post request inside Create_Student form
    @PostMapping("/students")
    public String savingStudent(@ModelAttribute Student student){
        //now  our form is filled with data we get student in request back, but we still need to save inside database
        studentRepo.save(student);
        //then returning the new view with created student
        return "redirect:/students";
    }


    // handling method if someone pressed update from the Students page(Main Page)
    @GetMapping("/students/edit/{id}")
    public String editStudentByID(@PathVariable("id") Long id, Model model){
        model.addAttribute("student",studentRepo.findById(id).get());
        // take your student and go to edit page and modify it.
        return "edit_Student";
    }


    //handling post method after edit_Student
    @PostMapping("/students/edit/{id}")
    public String editStudentByID( @ModelAttribute Student student, Model model){
        //Now this method received updated student from the user, we accessing it through the @ModelAttribute. Then, saving it inside the database.
        studentRepo.save(student);
        //returning to main page with updated student
        model.addAttribute("students",studentRepo.findAll());
        return "redirect:/students";
    }


    //when someone pressed delete button on main page, request get deleted here and repo get updated. returning the main page view.
    @GetMapping("/students/delete/{id}")
    public String deleteStudentByID(@PathVariable("id") Long id){
        studentRepo.deleteById(id);
        return "redirect:/students";
    }
}
