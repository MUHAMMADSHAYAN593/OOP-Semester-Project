package Questions;

import db.Con;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import Questions.DeepSeekMCQGenerator.MCQ;

public class QuizDAO {
    public List<MCQ> getQuestionsForQuiz(int quizId) {
        List<MCQ> questions = new ArrayList<>();
        String sql = "SELECT qb.id, qb.course, qb.question, qb.optionA, qb.optionB, qb.optionC, qb.optionD, qb.correctAnswer " +
                "FROM quiz_questions qq " +
                "JOIN question_bank qb ON qq.question_id = qb.id " +
                "WHERE qq.quiz_id = ? ORDER BY qq.question_order ASC"; // Order questions
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // 1. Create MCQ using your existing 6-argument constructor
                    MCQ mcq = new MCQ(
                            rs.getString("question"),
                            rs.getString("optionA"),
                            rs.getString("optionB"),
                            rs.getString("optionC"),
                            rs.getString("optionD"),
                            rs.getString("correctAnswer").charAt(0)
                    );
                    // 2. Set ID using its setter
                    mcq.setId(rs.getInt("id"));
                    // 3. Set Course using the new setter (assuming you've added it)
                    mcq.setCourse(rs.getString("course"));

                    questions.add(mcq);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving questions for quiz: " + e.getMessage());
            e.printStackTrace();
        }
        return questions;
    }

    // ... (rest of your QuizDAO methods) ...
    public int createQuiz(core.Quiz quiz) {
        String sql = "INSERT INTO quizzes (title, course, teacher_id, start_time, end_time, duration_minutes, total_questions, total_marks) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int quizId = 0;
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, quiz.getTitle());
            ps.setString(2, quiz.getCourse());
            ps.setString(3, quiz.getTeacherId());
            ps.setTimestamp(4, Timestamp.valueOf(quiz.getStartTime()));
            ps.setTimestamp(5, Timestamp.valueOf(quiz.getEndTime()));
            ps.setInt(6, quiz.getDurationMinutes());
            ps.setInt(7, quiz.getTotalQuestions());
            ps.setInt(8, quiz.getTotalMarks());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        quizId = rs.getInt(1);
                        quiz.setQuizId(quizId);
                        System.out.println("✅ Quiz '" + quiz.getTitle() + "' created with ID: " + quizId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error creating quiz: " + e.getMessage());
            e.printStackTrace();
        }
        return quizId;
    }

    public boolean addQuestionToQuiz(int quizId, int questionId, int questionOrder) {
        String sql = "INSERT INTO quiz_questions (quiz_id, question_id, question_order) VALUES (?, ?, ?)";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ps.setInt(2, questionId);
            ps.setInt(3, questionOrder);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("⚠️ Question " + questionId + " is already in Quiz " + quizId + ".");
            } else {
                System.err.println("❌ Error adding question to quiz: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    public core.Quiz getQuizById(int quizId) {
        String sql = "SELECT * FROM quizzes WHERE quiz_id = ?";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new core.Quiz(
                            rs.getInt("quiz_id"),
                            rs.getString("title"),
                            rs.getString("course"),
                            rs.getString("teacher_id"),
                            rs.getTimestamp("start_time").toLocalDateTime(),
                            rs.getTimestamp("end_time").toLocalDateTime(),
                            rs.getInt("duration_minutes"),
                            rs.getInt("total_questions"),
                            rs.getInt("total_marks")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving quiz by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<core.Quiz> getQuizzesByTeacherId(String teacherId) {
        List<core.Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes WHERE teacher_id = ?";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(new core.Quiz(
                            rs.getInt("quiz_id"),
                            rs.getString("title"),
                            rs.getString("course"),
                            rs.getString("teacher_id"),
                            rs.getTimestamp("start_time").toLocalDateTime(),
                            rs.getTimestamp("end_time").toLocalDateTime(),
                            rs.getInt("duration_minutes"),
                            rs.getInt("total_questions"),
                            rs.getInt("total_marks")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving quizzes by teacher ID: " + e.getMessage());
            e.printStackTrace();
        }
        return quizzes;
    }

    public int createQuizAttempt(QuizAttempt attempt) {
        String sql = "INSERT INTO quiz_attempts (student_id, quiz_id, attempt_start_time, is_submitted) VALUES (?, ?, ?, ?)";
        int attemptId = 0;
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, attempt.getStudentId());
            ps.setInt(2, attempt.getQuizId());
            ps.setTimestamp(3, Timestamp.valueOf(attempt.getAttemptStartTime()));
            ps.setBoolean(4, attempt.isSubmitted());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        attemptId = rs.getInt(1);
                        attempt.setAttemptId(attemptId);
                        System.out.println("✅ Attempt " + attemptId + " started for Student " + attempt.getStudentId() + " on Quiz " + attempt.getQuizId());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error creating quiz attempt: " + e.getMessage());
            e.printStackTrace();
        }
        return attemptId;
    }

    public boolean updateQuizAttempt(QuizAttempt attempt) {
        String sql = "UPDATE quiz_attempts SET attempt_end_time = ?, score = ?, is_submitted = ? WHERE attempt_id = ?";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, attempt.getAttemptEndTime() != null ? Timestamp.valueOf(attempt.getAttemptEndTime()) : null);
            ps.setObject(2, attempt.getScore(), java.sql.Types.INTEGER);
            ps.setBoolean(3, attempt.isSubmitted());
            ps.setInt(4, attempt.getAttemptId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating quiz attempt: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveStudentAnswer(StudentAnswer answer) {
        String sql = "INSERT INTO student_answers (attempt_id, question_id, student_selected_option, is_correct) VALUES (?, ?, ?, ?)";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, answer.getAttemptId());
            ps.setInt(2, answer.getQuestionId());
            ps.setString(3, String.valueOf(answer.getStudentSelectedOption()));
            ps.setObject(4, answer.getIsCorrect(), java.sql.Types.BOOLEAN);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        answer.setAnswerId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error saving student answer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<StudentAnswer> getStudentAnswersForAttempt(int attemptId) {
        List<StudentAnswer> answers = new ArrayList<>();
        String sql = "SELECT * FROM student_answers WHERE attempt_id = ?";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    answers.add(new StudentAnswer(
                            rs.getInt("answer_id"),
                            rs.getInt("attempt_id"),
                            rs.getInt("question_id"),
                            rs.getString("student_selected_option").charAt(0),
                            rs.getObject("is_correct", Boolean.class)
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving student answers for attempt: " + e.getMessage());
            e.printStackTrace();
        }
        return answers;
    }

    public boolean updateStudentAnswerCorrectStatus(int answerId, boolean isCorrect) {
        String sql = "UPDATE student_answers SET is_correct = ? WHERE answer_id = ?";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, isCorrect);
            ps.setInt(2, answerId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating student answer correct status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<core.Quiz> getAllQuizzes() {
        List<core.Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                quizzes.add(new core.Quiz(
                        rs.getInt("quiz_id"),
                        rs.getString("title"),
                        rs.getString("course"),
                        rs.getString("teacher_id"),
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime(),
                        rs.getInt("duration_minutes"),
                        rs.getInt("total_questions"),
                        rs.getInt("total_marks")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all quizzes: " + e.getMessage());
            e.printStackTrace();
        }
        return quizzes;
    }

    public List<QuizAttempt> getQuizAttemptsByStudentId(String studentId) {
        List<QuizAttempt> attempts = new ArrayList<>();
        String sql = "SELECT * FROM quiz_attempts WHERE student_id = ? ORDER BY attempt_start_time DESC";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    attempts.add(new QuizAttempt(
                            rs.getInt("attempt_id"),
                            rs.getString("student_id"),
                            rs.getInt("quiz_id"),
                            rs.getTimestamp("attempt_start_time").toLocalDateTime(),
                            rs.getTimestamp("attempt_end_time") != null ? rs.getTimestamp("attempt_end_time").toLocalDateTime() : null,
                            rs.getObject("score", Integer.class),
                            rs.getBoolean("is_submitted")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving quiz attempts for student: " + e.getMessage());
            e.printStackTrace();
        }
        return attempts;
    }

    public List<QuizAttempt> getQuizAttemptsByQuizId(int quizId) {
        List<QuizAttempt> attempts = new ArrayList<>();
        String sql = "SELECT * FROM quiz_attempts WHERE quiz_id = ? ORDER BY attempt_start_time DESC";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    attempts.add(new QuizAttempt(
                            rs.getInt("attempt_id"),
                            rs.getString("student_id"),
                            rs.getInt("quiz_id"),
                            rs.getTimestamp("attempt_start_time").toLocalDateTime(),
                            rs.getTimestamp("attempt_end_time") != null ? rs.getTimestamp("attempt_end_time").toLocalDateTime() : null,
                            rs.getObject("score", Integer.class),
                            rs.getBoolean("is_submitted")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving quiz attempts for quiz: " + e.getMessage());
            e.printStackTrace();
        }
        return attempts;
    }

    public boolean deleteQuiz(int quizId) {
        String sql = "DELETE FROM quizzes WHERE quiz_id = ?";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Quiz ID " + quizId + " and all associated data deleted successfully.");
                return true;
            } else {
                System.out.println("❌ Quiz ID " + quizId + " not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error deleting quiz: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}