package Questions; // Or src/Quiz

public class StudentAnswer {
    private int answerId;
    private int attemptId;
    private int questionId; // Corresponds to question_bank.id
    private char studentSelectedOption; // 'A', 'B', 'C', 'D'
    private Boolean isCorrect; // Use Boolean to allow null (if not yet marked)

    public StudentAnswer(int answerId, int attemptId, int questionId, char studentSelectedOption, Boolean isCorrect) {
        this.answerId = answerId;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.studentSelectedOption = studentSelectedOption;
        this.isCorrect = isCorrect;
    }

    // Constructor for a new answer (answerId will be set by DB, isCorrect is initial value)
    public StudentAnswer(int attemptId, int questionId, char studentSelectedOption) {
        this(0, attemptId, questionId, studentSelectedOption, null);
    }

    // Getters
    public int getAnswerId() { return answerId; }
    public int getAttemptId() { return attemptId; }
    public int getQuestionId() { return questionId; }
    public char getStudentSelectedOption() { return studentSelectedOption; }
    public Boolean getIsCorrect() { return isCorrect; }

    // Setters (for updating answer state, e.g., after marking)
    public void setAnswerId(int answerId) { this.answerId = answerId; }
    public void setIsCorrect(Boolean correct) { isCorrect = correct; }

    @Override
    public String toString() {
        return "Answer ID: " + answerId +
                "\nAttempt ID: " + attemptId +
                "\nQuestion ID: " + questionId +
                "\nSelected Option: " + studentSelectedOption +
                "\nIs Correct: " + (isCorrect != null ? (isCorrect ? "Yes" : "No") : "Not Marked");
    }
}