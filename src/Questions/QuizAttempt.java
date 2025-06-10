package Questions; // Or src/Quiz

import java.time.LocalDateTime;

public class QuizAttempt {
    private int attemptId;
    private String studentId;
    private int quizId;
    private LocalDateTime attemptStartTime;
    private LocalDateTime attemptEndTime; // Can be null if not yet submitted
    private Integer score; // Use Integer to allow null (if not yet marked)
    private boolean isSubmitted;

    public QuizAttempt(int attemptId, String studentId, int quizId, LocalDateTime attemptStartTime, LocalDateTime attemptEndTime, Integer score, boolean isSubmitted) {
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.quizId = quizId;
        this.attemptStartTime = attemptStartTime;
        this.attemptEndTime = attemptEndTime;
        this.score = score;
        this.isSubmitted = isSubmitted;
    }

    // Constructor for a new attempt (attemptId will be set by DB, score/endTime/isSubmitted are initial values)
    public QuizAttempt(String studentId, int quizId, LocalDateTime attemptStartTime) {
        this(0, studentId, quizId, attemptStartTime, null, null, false);
    }

    // Getters
    public int getAttemptId() { return attemptId; }
    public String getStudentId() { return studentId; }
    public int getQuizId() { return quizId; }
    public LocalDateTime getAttemptStartTime() { return attemptStartTime; }
    public LocalDateTime getAttemptEndTime() { return attemptEndTime; }
    public Integer getScore() { return score; }
    public boolean isSubmitted() { return isSubmitted; }

    // Setters (for updating attempt state)
    public void setAttemptId(int attemptId) { this.attemptId = attemptId; }
    public void setAttemptEndTime(LocalDateTime attemptEndTime) { this.attemptEndTime = attemptEndTime; }
    public void setScore(Integer score) { this.score = score; }
    public void setSubmitted(boolean submitted) { isSubmitted = submitted; }

    @Override
    public String toString() {
        return "Attempt ID: " + attemptId +
                "\nStudent ID: " + studentId +
                "\nQuiz ID: " + quizId +
                "\nStart Time: " + attemptStartTime +
                "\nEnd Time: " + (attemptEndTime != null ? attemptEndTime : "N/A") +
                "\nScore: " + (score != null ? score : "Not Marked") +
                "\nSubmitted: " + isSubmitted;
    }
}