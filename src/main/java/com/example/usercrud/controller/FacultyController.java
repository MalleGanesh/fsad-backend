package com.example.usercrud.controller;

import com.example.usercrud.model.Faculty;
import com.example.usercrud.repository.FacultyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {

    private final FacultyRepository facultyRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public FacultyController(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @GetMapping("/next-id")
    public Map<String, String> getNextFacultyId() {
        String maxId = facultyRepository.findMaxFacultyId();
        int nextId = 101; // Default start
        if (maxId != null) {
            try {
                nextId = Integer.parseInt(maxId) + 1;
            } catch (NumberFormatException e) {
                // If it's not a number, we might need a different strategy, 
                // but based on "101" placeholder, this should work.
            }
        }
        return Map.of("nextId", String.valueOf(nextId));
    }

    @GetMapping
    public List<Map<String, Object>> getFaculty(@RequestParam(required = false) Boolean enabled) {
        List<Faculty> facultyList = Boolean.TRUE.equals(enabled)
                ? facultyRepository.findByEnabledTrue()
                : facultyRepository.findAll();

        return facultyList.stream()
                .map(f -> {
                    Map<String, Object> facultyData = new HashMap<>();
                    facultyData.put("id", f.getId());
                    facultyData.put("name", f.getName());
                    facultyData.put("facultyId", f.getFacultyId());
                    facultyData.put("course", f.getCourse());
                    facultyData.put("enabled", f.isEnabled());
                    return facultyData;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String facultyId = payload.get("facultyId");
        String password = payload.get("password");
        String course = payload.get("course");

        if (name == null || name.isBlank() || facultyId == null || facultyId.isBlank() || password == null || password.isBlank() || course == null || course.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Name, Faculty ID, Password, and Course are required"));
        }

        if (facultyRepository.findByFacultyId(facultyId.trim().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Faculty ID " + facultyId + " already exists. Please refresh for a new one."));
        }

        Faculty faculty = new Faculty();
        faculty.setName(name.trim());
        faculty.setFacultyId(facultyId.trim().toLowerCase());
        faculty.setPassword(passwordEncoder.encode(password));
        faculty.setCourse(course.trim());
        faculty.setEnabled(false);
        facultyRepository.save(faculty);

        return ResponseEntity.ok(Map.of(
                "message", "Faculty signup successful. Waiting for admin approval."
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String facultyId = payload.get("facultyId");
        String password = payload.get("password");

        if (facultyId == null || facultyId.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Faculty ID and password are required"));
        }

        Optional<Faculty> facultyOpt = facultyRepository.findByFacultyId(facultyId.trim().toLowerCase());
        if (facultyOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid faculty credentials"));
        }

        Faculty faculty = facultyOpt.get();
        if (!faculty.isEnabled()) {
            return ResponseEntity.status(403).body(Map.of("message", "Access not granted by admin"));
        }

        if (!passwordEncoder.matches(password, faculty.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid faculty credentials"));
        }

        return ResponseEntity.ok(Map.of(
                "id", faculty.getId(),
                "name", faculty.getName(),
                "facultyId", faculty.getFacultyId(),
                "course", faculty.getCourse(),
                "enabled", faculty.isEnabled()
        ));
    }

    @PatchMapping("/{id}/access")
    public ResponseEntity<?> updateAccess(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Optional<Faculty> facultyOpt = facultyRepository.findById(id);
        if (facultyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Boolean enabled = payload.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "enabled field is required"));
        }

        Faculty faculty = facultyOpt.get();
        faculty.setEnabled(enabled);
        facultyRepository.save(faculty);

        return ResponseEntity.ok(Map.of(
                "id", faculty.getId(),
                "name", faculty.getName(),
                "facultyId", faculty.getFacultyId(),
                "course", faculty.getCourse(),
                "enabled", faculty.isEnabled()
        ));
    }
}
