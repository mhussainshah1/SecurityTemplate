package com.example.demo.business.entities.repositories;

import com.example.demo.business.entities.Course;
import com.example.demo.business.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findAllByUser(User user, Pageable pageable);
    //Search by Title or description
    Page<Course> findAllByTitleContainingOrDescriptionContainingAllIgnoreCase(String title, String description, Pageable pageable);
    Page<Course> findAllByUserAndTitleContainingOrUserAndDescriptionContainingAllIgnoreCase(User user1, String title, User user, String description, Pageable pageable);
}
