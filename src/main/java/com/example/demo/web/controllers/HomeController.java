package com.example.demo.web.controllers;

import com.example.demo.business.entities.Course;
import com.example.demo.business.entities.User;
import com.example.demo.business.entities.repositories.CourseRepository;
import com.example.demo.business.entities.repositories.UserRepository;
import com.example.demo.business.services.CustomerUserDetails;
import com.example.demo.business.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    public String listItems(Model model, @RequestParam(defaultValue = "0") int page) {
        var user = userService.getUser();
        /**
         * Alternative way to get user
         *-----------------------------
         *  var myuser = ((CustomerUserDetails)
         *                 ((UsernamePasswordAuthenticationToken) principal)
         *                         .getPrincipal())
         *                 .getUsers();
         */
        if (user != null) {
            model.addAttribute("user_id", userService.getUser().getId());
            if (userService.isUser()) {
                model.addAttribute("courses", courseRepository.findAllByUser(user, PageRequest.of(page, 4)));
            }
            if (userService.isAdmin()) {
                model.addAttribute("courses", courseRepository.findAll(PageRequest.of(page, 4)));
            }
        } else {
            model.addAttribute("courses", courseRepository.findAll(PageRequest.of(page, 4)));
        }
        model.addAttribute("currentPage", page);
        return "list";
    }

    //Users with Admin role can view this page
    @RequestMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin";
    }

    //AUXILLARY FUNCTION!!!
    //Use the below code INSIDE METHOD to pass user into view
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
    public String updateCourse(@PathVariable("id") long id,
                               Model model) {
        model.addAttribute("course", courseRepository.findById(id).get());
        return "courseform";
    }

    @RequestMapping("/delete/{id}")
    public String delCourse(@PathVariable("id") long id) {
        courseRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/check")
    public String check(@RequestParam("check") long[] ids,
                        @RequestParam("name") String name,
                        Model model) {
        // delete selected
        if (name.equals("delete")) {
            for (var id : ids) {
                courseRepository.deleteById(id);
            }
            return "redirect:/";
        }
        return "list";
    }

    @GetMapping("/about")
    public String getAbout() {
        return "about";
    }
}
