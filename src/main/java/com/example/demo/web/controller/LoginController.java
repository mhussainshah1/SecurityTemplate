package com.example.demo.web.controller;

import com.example.demo.business.entities.User;
import com.example.demo.business.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/forgot-password")
    public String forgetPassword() {
        return "/";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/termsandconditions")
    public String getTermsAndCondition() {
        return "termsandconditions";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, @RequestParam("password") String pw) {
        System.out.println("pw: " + pw);
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        } else {
            user.setPassword(userService.encode(pw));
            userService.saveUser(user);
            model.addAttribute("message", "New User Account Created");
        }
        return "login";
    }
}
