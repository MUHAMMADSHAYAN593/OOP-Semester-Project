package core;

import java.time.LocalDateTime;

public class Quiz {
    private int quizId;
    private String title;
    private String course;
    private String teacherId;
    private LocalDateTime startTime; // This is LocalDateTime
    private LocalDateTime endTime;   // This is LocalDateTime
    private int durationMinutes;
    private int totalQuestions;
    private int totalMarks;

    public Quiz(int quizId, String title, String course, String teacherId, LocalDateTime startTime, LocalDateTime endTime, int durationMinutes, int totalQuestions, int totalMarks) {
        this.quizId = quizId;
        this.title = title;
        this.course = course;
        this.teacherId = teacherId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.totalQuestions = totalQuestions;
        this.totalMarks = totalMarks;
    }

    // Constructor without quizId for new quizzes
    public Quiz(String title, String course, String teacherId, LocalDateTime startTime, LocalDateTime endTime, int durationMinutes, int totalQuestions, int totalMarks) {
        this(0, title, course, teacherId, startTime, endTime, durationMinutes, totalQuestions, totalMarks);
    }

    // Getters
    public int getQuizId() { return quizId; }
    public String getTitle() { return title; }
    public String getCourse() { return course; }
    public String getTeacherId() { return teacherId; }
    public LocalDateTime getStartTime() { return startTime; } // Returns LocalDateTime
    public LocalDateTime getEndTime() { return endTime; }     // Returns LocalDateTime
    public int getDurationMinutes() { return durationMinutes; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getTotalMarks() { return totalMarks; }

    // Setter for quizId
    public void setQuizId(int quizId) { this.quizId = quizId; }
}