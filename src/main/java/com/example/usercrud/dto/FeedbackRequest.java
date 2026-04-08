package com.example.usercrud.dto;

public class FeedbackRequest {
    private String studentName;
    private String studentEmail;
    private Long facultyId;
    private String courseName;
    private int q1;
    private int q2;
    private int q3;
    private int q4;
    private int q5;
    private String comment;

    // Getters and Setters
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public Long getFacultyId() { return facultyId; }
    public void setFacultyId(Long facultyId) { this.facultyId = facultyId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getQ1() { return q1; }
    public void setQ1(int q1) { this.q1 = q1; }

    public int getQ2() { return q2; }
    public void setQ2(int q2) { this.q2 = q2; }

    public int getQ3() { return q3; }
    public void setQ3(int q3) { this.q3 = q3; }

    public int getQ4() { return q4; }
    public void setQ4(int q4) { this.q4 = q4; }

    public int getQ5() { return q5; }
    public void setQ5(int q5) { this.q5 = q5; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
