package com.example.demo.business.services;

import com.example.demo.business.entities.Course;
import com.example.demo.business.entities.InvalidPassword;
import com.example.demo.business.entities.Role;
import com.example.demo.business.entities.User;
import com.example.demo.business.entities.repositories.CourseRepository;
import com.example.demo.business.entities.repositories.InvalidPasswordRepository;
import com.example.demo.business.entities.repositories.RoleRepository;
import com.example.demo.business.entities.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;

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
        var passwords = new HashSet<InvalidPassword>();
        passwords.add(new InvalidPassword("azerty12!"));
        passwords.add(new InvalidPassword("12345678!"));
        passwords.add(new InvalidPassword("password123"));
        invalidPasswordRepository.saveAll(passwords);

        //Role
        roleRepository.save(new Role("USER"));
        roleRepository.save(new Role("ADMIN"));

        var userRole = roleRepository.findByRole("USER");
        var adminRole = roleRepository.findByRole("ADMIN");

        //User
        var admin = new User("admin@admin.com", userService.encode("password"), "Admin", "User", true, "admin");
        userService.saveAdmin(admin);

        var moe = new User("mhussainshah79@gmail.com", userService.encode("password"), "Muhammad", "Shah", true, "moe");
        userService.saveUser(moe);

        var lan = new User("tolani.oyefule@gmail.com", userService.encode("password"), "Tolani", "Oyefule", true, "lan");
        userService.saveUser(lan);

        var nan = new User("nhan.cog.huynh@gmail.com", userService.encode("password"), "Nhan", "Huynh", true, "nan");
        userService.saveUser(nan);

        var dag = new User("dag@gmail.com", userService.encode("password"), "Dag", "Fasil", true, "dag");
        userService.saveUser(dag);

        var mel = new User("melissafong@gmail.com", userService.encode("password"), "Mellisa", "Lavander", true, "mel");
        userService.saveUser(mel);

        var jen = new User("jen@gmail.com", userService.encode("password"), "Jennifer", "You", true, "jen");
        userService.saveUser(jen);

        Course course = new Course("Astrophysics", "Neil D Tyson", "Just a course on stars", 3);
        course.setUser(nan);
        courseRepository.save(course);

        course = new Course("Calculus", "Carol Henley", "Rate of change of rate of change", 3);
        course.setUser(moe);
        courseRepository.save(course);

        course = new Course("Freshman English", "Geraldine Pegram", "Learn your language chilern", 3);
        course.setUser(lan);
        courseRepository.save(course);
    }
}
