package com.example.usercrud.repository;

import com.example.usercrud.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByFacultyId(String facultyId);
    List<Faculty> findByEnabledTrue();
    
    @org.springframework.data.jpa.repository.Query("SELECT MAX(f.facultyId) FROM Faculty f")
    String findMaxFacultyId();
}
