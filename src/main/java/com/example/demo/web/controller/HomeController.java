package com.example.demo.web.controller;

import com.example.demo.business.CustomerUserDetails;
import com.example.demo.business.entities.Course;
import com.example.demo.business.entities.User;
import com.example.demo.business.entities.repositories.CourseRepository;
import com.example.demo.business.entities.repositories.UserRepository;
import com.example.demo.business.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class HomeController {
    @Autowired
    CourseRepository courseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @RequestMapping("/")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseRepository.findAll()); //generate select * statement
        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        return "list";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user,
                                          BindingResult result,
                                          Model model,
                                          @RequestParam("password") String pw) {
        System.out.println("pw: " + pw);
        model.addAttribute("user", user);
        if (result.hasErrors()) {
            return "register";
        } else {
            user.encode(pw);
            userService.saveUser(user);
            model.addAttribute("message", "New User Account Successfully Created");
        }
        return "login";
    }


    @RequestMapping("/updateUser")
    public String viewUser(Model model,
//                           HttpServletRequest request,
//                           Authentication authentication,
                           Principal principal) {
//        Boolean isAdmin = request.isUserInRole("ADMIN");
//        Boolean isUser = request.isUserInRole("USER");
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String username = principal.getName();
//        model.addAttribute("user", userRepository.findByUsername(username));
        return "register";
    }

    //ASK DAVE!!!
    @RequestMapping("/admin")
    public String admin() {
        return "admin";
    }

    //ASK DAVE!!!
    @RequestMapping("/secure")
    public String secure(Principal principal, Model model) {
        User myuser = ((CustomerUserDetails)
                ((UsernamePasswordAuthenticationToken) principal)
                        .getPrincipal())
                .getUser();
        model.addAttribute("myuser", myuser);
        return "secure";
    }

    @GetMapping("/add")
    public String courseForm(Model model) {
        model.addAttribute("course", new Course());
        return "courseform";
    }

    @PostMapping("/process")
    public String processForm(@Valid Course course, BindingResult result) {
        if (result.hasErrors()) {
            return "courseform";
        }

        course.setUser(userService.getUser());
        courseRepository.save(course);//generate SQL statement and insert into database
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showCourse(@PathVariable("id") long id, Model model) {
        model.addAttribute("course", courseRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateCourse(@PathVariable("id") long id, Model model) {
        model.addAttribute("course", courseRepository.findById(id).get());
        return "courseform";
    }

    @RequestMapping("/delete/{id}")
    public String delCourse(@PathVariable("id") long id) {
        courseRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/termsandconditions")
    public String getTermsAndCondition() {
        return "termsandconditions";
    }

    @PostMapping("/forgot-password")
    public String forgetPassword() {
        return "/";
    }

    @GetMapping("/about")
    public String getAbout() {
        return "about";
    }
}
