package com.example.usercrud;

import com.example.usercrud.model.Faculty;
import com.example.usercrud.model.Student;
import com.example.usercrud.repository.FacultyRepository;
import com.example.usercrud.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class StudentFeedbackBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentFeedbackBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            
            // Seed Faculty
            createFacultyIfMissing(facultyRepository, "101", "RESHMA", "Java Programming", encoder);
            createFacultyIfMissing(facultyRepository, "102", "Dr. R. Saxena", "Artificial Intelligence", encoder);
            createFacultyIfMissing(facultyRepository, "103", "Dr. Hari Pothuluru", "Software Engineering", encoder);
            createFacultyIfMissing(facultyRepository, "104", "Dr. Maneesha Vadduri", "Python Programming", encoder);
            createFacultyIfMissing(facultyRepository, "105", "Dr. Nichenametla Rajesh", "Operating Systems", encoder);
            createFacultyIfMissing(facultyRepository, "106", "Dr. K. Srinivas", "Compiler Design", encoder);
            createFacultyIfMissing(facultyRepository, "110", "DR.LAKSHMI DEVI", "CYBER SECURITY", encoder);

            // Cleanup: remove the duplicate small-letter faculty 107 if it exists
            facultyRepository.findByFacultyId("107").ifPresent(facultyRepository::delete);

            // Seed/Fix Students
            createStudentIfMissing(studentRepository, "ganesh@gmail.com", "Ganesh", "B.Tech", encoder);
            createStudentIfMissing(studentRepository, "malleganeshganesh@gmail.com", "Ganesh", "B.Tech", encoder);
        };
    }

    private void createFacultyIfMissing(
            FacultyRepository facultyRepository,
            String facultyId,
            String name,
            String course,
            BCryptPasswordEncoder encoder
    ) {
        facultyRepository.findByFacultyId(facultyId).ifPresentOrElse(
            faculty -> {
                // Update course if it's currently "General", null, or blank
                if (faculty.getCourse() == null || faculty.getCourse().isBlank() || faculty.getCourse().equalsIgnoreCase("General")) {
                    faculty.setCourse(course);
                    facultyRepository.save(faculty);
                }
            },
            () -> {
                Faculty faculty = new Faculty(name, facultyId, encoder.encode("faculty@123"), false, course);
                facultyRepository.save(faculty);
            }
        );
    }

    private void createStudentIfMissing(
            StudentRepository studentRepository,
            String email,
            String name,
            String course,
            BCryptPasswordEncoder encoder
    ) {
        studentRepository.findByEmail(email).ifPresentOrElse(
            student -> {
                if (student.getPassword() == null || student.getPassword().isBlank()) {
                    student.setPassword(encoder.encode("Ganesh@123"));
                    studentRepository.save(student);
                }
            },
            () -> {
                Student student = new Student(name, email, encoder.encode("Ganesh@123"), course);
                studentRepository.save(student);
            }
        );
    }
}