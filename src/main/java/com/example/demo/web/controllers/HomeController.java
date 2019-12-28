package com.example.demo.web.controllers;

import com.cloudinary.utils.ObjectUtils;
import com.example.demo.business.entities.Course;
import com.example.demo.business.entities.repositories.CourseRepository;
import com.example.demo.business.entities.repositories.UserRepository;
import com.example.demo.business.services.UserService;
import com.example.demo.web.config.CloudinaryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Controller
public class HomeController {
    @Autowired
    CourseRepository courseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CloudinaryConfig cloudc;

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

    @GetMapping("/add")
    public String courseForm(Model model) {
        model.addAttribute("imageLabel", "Upload Image");
        model.addAttribute("myuser", userService.getUser());
        model.addAttribute("course", new Course());
        return "courseform";
    }

    @PostMapping("/process")
    public String processForm(@Valid @ModelAttribute("course") Course course,
                              BindingResult result,
                              @RequestParam("file") MultipartFile file,
                              Model model) {
        model.addAttribute("imageLabel", "Upload Image");
        model.addAttribute("myuser", userService.getUser());
        if (result.hasErrors()) {
            return "courseform";
        }
        if (!file.isEmpty()) {
            try {
                var uploadResultMap = cloudc.upload(
                        file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
                var url = uploadResultMap.get("url").toString();
                var uploadedName = uploadResultMap.get("public_id").toString();
                var transformedImage = cloudc.createUrl(uploadedName, 150, 150);
                course.setPicturePath(transformedImage);
                course.setUser(userService.getUser());
            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/add";
            }
        } else {
            //if file is empty and there is a picture path then save course
            if (course.getPicturePath().isEmpty()) {
                return "courseform";
            }
        }
        courseRepository.save(course); //generate SQL statement and insert into database
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showCourse(@PathVariable("id") long id, Model model) {
        model.addAttribute("course", courseRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateCourse(@PathVariable("id") long id,
                               @ModelAttribute Course course,
                               Model model) {
        course = courseRepository.findById(id).get();
        model.addAttribute("myuser", userService.getUser());
        model.addAttribute("course", courseRepository.findById(id).get());

        if (course.getPicturePath().isEmpty()) {
            model.addAttribute("imageLabel", "Upload Image");
        } else {
            model.addAttribute("imageLabel", "Upload New Image");
        }
        return "courseform";
    }

    @RequestMapping("/delete/{id}")
    public String delCourse(@PathVariable("id") long id) {
        courseRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/check")
    public String check(@RequestParam("check") long[] ids,
                        Model model) {
        for (var id : ids) {
            courseRepository.deleteById(id);
        }
        return "redirect:/";
    }

    @GetMapping("/about")
    public String getAbout() {
        return "about";
    }

    @GetMapping("/search")
    public String searchword(Model model,
                             @RequestParam String search,
                             @RequestParam(defaultValue = "0") int page) {
        var user = userService.getUser();
        var searchCourses =
                courseRepository.findAllByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, PageRequest.of(page, 4));
        if (user != null) {
            if (userService.isUser()) {
                model.addAttribute("courses",
                        courseRepository
                                .findAllByUserAndTitleContainingOrUserAndDescriptionContainingAllIgnoreCase(user, search, user, search, PageRequest.of(page, 4)));
            }
            if (userService.isAdmin()) {
                model.addAttribute("courses", searchCourses);
            }
        } else {
            model.addAttribute("courses", searchCourses);
        }
        model.addAttribute("searchString", search);
        model.addAttribute("currentPage", page);
        return "list";
    }
}
