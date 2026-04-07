package com.example.usercrud.repository;

import com.example.usercrud.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByFaculty_Name(String facultyName);
}