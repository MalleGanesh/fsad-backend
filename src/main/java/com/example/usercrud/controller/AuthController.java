package com.example.usercrud.controller;

import com.example.usercrud.dto.LoginRequest;
import com.example.usercrud.dto.RegisterRequest;
import com.example.usercrud.model.Student;
import com.example.usercrud.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final StudentRepository studentRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Value("${app.admin.username:admin}")
    private String adminUsername;
    @Value("${app.admin.password:admin123456}")
    private String adminPassword;

    public AuthController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Check if email already exists
        String email = request.getEmail().trim().toLowerCase();
        if (studentRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email is already registered"));
        }

        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
        student.setCourse(request.getCourse());

        studentRepository.save(student);

        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        Optional<Student> studentOpt = studentRepository.findByEmail(email);

        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid email or password"));
        }

        Student student = studentOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid email or password"));
        }

        // Return basic user info (never return the password)
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "id", student.getId(),
                "name", student.getName(),
                "email", student.getEmail(),
                "course", student.getCourse()
        ));
    }

    // POST /api/auth/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }
        return ResponseEntity.ok(Map.of(
                "message", "If the account exists, a reset link has been sent"
        ));
    }

    // POST /api/auth/admin/login
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password are required"));
        }

        String targetUsername = adminUsername.trim();
        String targetPassword = adminPassword.trim();

        System.out.println("DEBUG: Admin Login Attempt - Username: ["+username+"]");

        if (!targetUsername.equalsIgnoreCase(username.trim()) || !targetPassword.equals(password.trim())) {
            System.out.println("DEBUG: Login Failed. Expected: ["+targetUsername+"] / ["+targetPassword+"]");
            return ResponseEntity.status(401).body(Map.of("message", "Invalid Admin Credentials"));
        }

        return ResponseEntity.ok(Map.of("message", "Admin login successful"));
    }
}
