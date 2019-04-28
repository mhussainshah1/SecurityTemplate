package com.example.demo;

import com.example.demo.business.entities.Course;
import com.example.demo.business.entities.InvalidPassword;
import com.example.demo.business.entities.Role;
import com.example.demo.business.entities.User;
import com.example.demo.business.entities.repositories.CourseRepository;
import com.example.demo.business.entities.repositories.InvalidPasswordRepository;
import com.example.demo.business.entities.repositories.RoleRepository;
import com.example.demo.business.entities.repositories.UserRepository;
import com.example.demo.business.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    InvalidPasswordRepository invalidPasswordRepository;

    @Autowired
    UserService userService;

    @Override
    public void run(String... args) throws Exception {
        //Password
        Set<InvalidPassword> passwords = new HashSet<>();
        passwords.add(new InvalidPassword("azerty12!"));
        passwords.add(new InvalidPassword("12345678!"));
        passwords.add(new InvalidPassword("password123"));
        invalidPasswordRepository.saveAll(passwords);

        //Role
        roleRepository.save(new Role("USER"));
        roleRepository.save(new Role("ADMIN"));

        Role userRole = roleRepository.findByRole("USER");
        Role adminRole = roleRepository.findByRole("ADMIN");

        //User
        User moe = new User("mhussainshah79@gmail.com", "password", "Muhammad", "Shah", true, "moe");
        moe.setPassword(userService.encode(moe.getPassword()));
        userService.saveUser(moe);

        User nan = new User("nhan.cog.huynh@gmail.com", "password", "Nhan", "Huynh", true, "nan");
        nan.setPassword(userService.encode(nan.getPassword()));
        userService.saveUser(nan);

        User tolani = new User("tolani.oyefule@gmail.com", "password", "Tolani", "Oyefule", true, "lan");
        tolani.setPassword(userService.encode(tolani.getPassword()));
        userService.saveUser(tolani);

        User admin = new User("admin@admin.com", "Pa$$word2019", "Admin", "User", true, "admin");
        admin.setPassword(userService.encode(admin.getPassword()));
        userService.saveUser(admin);

        Course course = new Course("Astrophysics", "Neil D Tyson", "Just a course on stars", 3);
        course.setUser(nan);
        courseRepository.save(course);

        course = new Course("Calculus", "Carol Henley", "Rate of change of rate of change", 3);
        course.setUser(moe);
        courseRepository.save(course);

        course = new Course("Freshman English", "Geraldine Pegram", "Learn your language chilern", 3);
        course.setUser(tolani);
        courseRepository.save(course);
    }
}
