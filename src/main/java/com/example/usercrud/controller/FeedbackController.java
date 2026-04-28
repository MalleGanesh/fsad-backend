package com.example.usercrud.controller;

import com.example.usercrud.dto.FeedbackRequest;
import com.example.usercrud.model.Faculty;
import com.example.usercrud.model.Feedback;
import com.example.usercrud.repository.FacultyRepository;
import com.example.usercrud.repository.FeedbackRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final FacultyRepository facultyRepository;

    public FeedbackController(FeedbackRepository feedbackRepository, FacultyRepository facultyRepository) {
        this.feedbackRepository = feedbackRepository;
        this.facultyRepository = facultyRepository;
    }

    // GET all feedback
    @GetMapping
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    // GET feedback by faculty name
    @GetMapping("/faculty/{facultyName}")
    public List<Feedback> getFeedbackByFaculty(@PathVariable String facultyName) {
        return feedbackRepository.findByFaculty_Name(facultyName);
    }

    // GET feedback by student email
    @GetMapping("/student/{email}")
    public List<Feedback> getFeedbackByStudentEmail(@PathVariable String email) {
        return feedbackRepository.findByStudentEmail(email);
    }

    // POST feedback
    @PostMapping
    public ResponseEntity<?> createFeedback(@RequestBody FeedbackRequest request) {
        if (request.getQ1() < 1 || request.getQ1() > 5 ||
            request.getQ2() < 1 || request.getQ2() > 5 ||
            request.getQ3() < 1 || request.getQ3() > 5 ||
            request.getQ4() < 1 || request.getQ4() > 5 ||
            request.getQ5() < 1 || request.getQ5() > 5) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "Ratings must be between 1 and 5"));
        }

        if (feedbackRepository.existsByStudentEmailAndFaculty_Id(request.getStudentEmail(), request.getFacultyId())) {
            return ResponseEntity.status(400).body(java.util.Map.of("message", "Feedback already given for this faculty"));
        }

        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + request.getFacultyId()));

        Feedback feedback = new Feedback();
        feedback.setStudentName(request.getStudentName());
        feedback.setStudentEmail(request.getStudentEmail());
        feedback.setFaculty(faculty);
        feedback.setCourseName(request.getCourseName());
        feedback.setQ1(request.getQ1());
        feedback.setQ2(request.getQ2());
        feedback.setQ3(request.getQ3());
        feedback.setQ4(request.getQ4());
        feedback.setQ5(request.getQ5());
        feedback.setComment(request.getComment());

        return ResponseEntity.ok(feedbackRepository.save(feedback));
    }

}