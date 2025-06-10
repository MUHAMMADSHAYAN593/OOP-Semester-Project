package users;

import db.Con;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import core.Quiz;

import java.time.LocalDateTime;

public class Student {
    private String id;
    private String name;
    private String password;
    private String course;

    public Student(String id, String name, String password, String course) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.course = course;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getCourse() {
        return course;
    }

    // ✅ Get student by ID from database
    public static Student getStudentById(String id) {
        String query = "SELECT * FROM students WHERE id = ?";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Student(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("course")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error retrieving student: " + e.getMessage());
        }
        return null;
    }

    // ✅ Get all students from database
    public static List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                students.add(new Student(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("course")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error retrieving students: " + e.getMessage());
        }
        return students;
    }

    public List<Quiz> getAvailableQuizzes() {
        List<Quiz> availableQuizzes = new ArrayList<>();
        String query = "SELECT * FROM quizzes WHERE course = ? AND start_time <= ? AND end_time >= ?";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, this.course);
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            ps.setTimestamp(2, now);
            ps.setTimestamp(3, now);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Quiz quiz = new Quiz(
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
                availableQuizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to retrieve available quizzes: " + e.getMessage());
        }
        return availableQuizzes;
    }
    public int startQuizAttempt(int quizId) {
        String query = "INSERT INTO quiz_attempts (student_id, quiz_id, attempt_start_time, is_submitted) VALUES (?, ?, ?, ?)";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.id);
            ps.setInt(2, quizId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setBoolean(4, false);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to start quiz attempt: " + e.getMessage());
        }
        return -1; // Indicate failure
    }

    public void recordStudentAnswer(int attemptId, int questionId, String studentSelectedOption) {
        String query = "INSERT INTO student_answers (attempt_id, question_id, student_selected_option) VALUES (?, ?, ?)";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, attemptId);
            ps.setInt(2, questionId);
            ps.setString(3, studentSelectedOption);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Failed to record answer: " + e.getMessage());
        }
    }

    public void submitQuizAttempt(int attemptId) {
        String query = "UPDATE quiz_attempts SET attempt_end_time = ?, is_submitted = ? WHERE attempt_id = ?";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setBoolean(2, true);
            ps.setInt(3, attemptId);
            ps.executeUpdate();
            System.out.println("✅ Quiz submitted successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Failed to submit quiz: " + e.getMessage());
        }
    }
}
