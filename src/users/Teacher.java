package users;

import db.Con;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Questions.QuestionBank;

public class Teacher {
    private String id;
    private String name;
    private String password;
    private String course;

    public Teacher(String id, String name, String pass, String course) {
        this.id = id;
        this.name = name;
        this.password = pass;
        this.course = course;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public String getPassword() { return password; }

    public String getCourse() { return course; }

    public void setCourse(String course) { this.course = course; }

    public boolean login(String inputId, String inputPass) {
        return this.id.equals(inputId) && this.password.equals(inputPass);
    }

    public void assignMarks(String studentId, int marks) {
        String query = "INSERT INTO marks (student_id, teacher_id, marks) VALUES (?, ?, ?)";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, studentId);
            ps.setString(2, id);
            ps.setInt(3, marks);
            ps.executeUpdate();
            System.out.println("✅ Marks assigned: " + marks + " to Student ID: " + studentId);
        } catch (SQLException e) {
            System.out.println("❌ Failed to assign marks: " + e.getMessage());
        }
    }

    public void viewProfile() {
        System.out.println("Teacher ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Course: " + (course != null ? course : "Not assigned"));
    }

    public void editProfile(String newName, String newCourse) {
        String query = "UPDATE teachers SET name = ?, course = ? WHERE id = ?";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, newName);
            ps.setString(2, newCourse);
            ps.setString(3, id);
            ps.executeUpdate();
            this.name = newName;
            this.course = newCourse;
            System.out.println("✅ Profile updated.");
        } catch (SQLException e) {
            System.out.println("❌ Failed to update profile: " + e.getMessage());
        }
    }

    public List<String> getAssignedStudents() {
        List<String> assigned = new ArrayList<>();
        String query = "SELECT id, name FROM students WHERE course = ?";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, course);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                assigned.add(rs.getString("name") + " (ID: " + rs.getString("id") + ")");
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to retrieve students: " + e.getMessage());
        }
        return assigned;
    }

    public void addAnnouncement(String message, Timestamp dateTime) {
        // Updated query to include announcement_datetime column
        String query = "INSERT INTO announcements (teacher_id, message, announcement_datetime) VALUES (?, ?, ?)";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            ps.setString(2, message);
            ps.setTimestamp(3, dateTime); // Set the Timestamp
            ps.executeUpdate();
            System.out.println("✅ Announcement posted with date/time: " + dateTime);
        } catch (SQLException e) {
            System.out.println("❌ Failed to post announcement: " + e.getMessage());
        }
    }

    public List<String> getAnnouncements() {
        List<String> announcements = new ArrayList<>();
        // Select message AND announcement_datetime
        String query = "SELECT message, announcement_datetime FROM announcements WHERE teacher_id = ? ORDER BY announcement_datetime DESC";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String message = rs.getString("message");
                Timestamp datetime = rs.getTimestamp("announcement_datetime");
                // Format the output to include date/time
                announcements.add("📢 [" + (datetime != null ? datetime.toString().split("\\.")[0] : "N/A") + "] " + message);
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to load announcements: " + e.getMessage());
        }
        return announcements;
    }

    public void createQuiz(String title, String course, Timestamp startTime, Timestamp endTime, int durationMinutes, int numQuestionsToSelect, int totalMarks) {
        String insertQuizQuery = "INSERT INTO quizzes (title, course, teacher_id, start_time, end_time, duration_minutes, total_questions, total_marks) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertQuizQuestionsQuery = "INSERT INTO quiz_questions (quiz_id, question_id) VALUES (?, ?)";

        try (Connection con = Con.getConnection()) {
            con.setAutoCommit(false); // Start transaction

            // 1. Insert quiz metadata
            int quizId = -1;
            try (PreparedStatement ps = con.prepareStatement(insertQuizQuery, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, title);
                ps.setString(2, course);
                ps.setString(3, this.id); // Teacher's own ID
                ps.setTimestamp(4, startTime);
                ps.setTimestamp(5, endTime);
                ps.setInt(6, durationMinutes);
                ps.setInt(7, numQuestionsToSelect); // Store actual number of questions
                ps.setInt(8, totalMarks);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    quizId = rs.getInt(1); // Get the auto-generated quiz_id
                } else {
                    throw new SQLException("Creating quiz failed, no ID obtained.");
                }
            }

            // 2. Select questions from QuestionBank and link them to the quiz
            if (quizId != -1) {
                // Fetch all question IDs for the given course
                List<Integer> availableQuestionIds = QuestionBank.getQuestionIdsByCourse(course);
                if (availableQuestionIds.isEmpty()) {
                    System.out.println("❌ No questions available in the Question Bank for course: " + course);
                    con.rollback(); // Rollback transaction
                    return;
                }

                // Shuffle and pick desired number of questions
                Collections.shuffle(availableQuestionIds);
                List<Integer> selectedQuestionIds = new ArrayList<>();
                for (int i = 0; i < Math.min(numQuestionsToSelect, availableQuestionIds.size()); i++) {
                    selectedQuestionIds.add(availableQuestionIds.get(i));
                }

                if (selectedQuestionIds.isEmpty()) {
                    System.out.println("❌ Could not select enough questions for the quiz from the Question Bank.");
                    con.rollback();
                    return;
                }

                // Insert selected questions into quiz_questions table
                try (PreparedStatement ps = con.prepareStatement(insertQuizQuestionsQuery)) {
                    for (int questionId : selectedQuestionIds) {
                        ps.setInt(1, quizId);
                        ps.setInt(2, questionId);
                        ps.addBatch(); // Add to batch for efficient insertion
                    }
                    ps.executeBatch(); // Execute all batched inserts
                }
                System.out.println("✅ Quiz '" + title + "' created successfully with " + selectedQuestionIds.size() + " questions!");
                con.commit(); // Commit transaction
            } else {
                con.rollback(); // Rollback if quizId wasn't generated
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to create quiz: " + e.getMessage());
            // No need to rollback here if we started a transaction and it's already caught by the outer try-catch
            // The outer try-catch will only be hit if the first try block (for quiz insert) fails before auto-commit is off,
            // or if a connection issue occurs. We explicitly handle rollback within the try block.
        }
    }


    // ... (rest of your Teacher class methods)

    public void createExam(String examTitle) {
        // This method is now likely obsolete given the new createQuiz method.
        // You might want to remove or refactor it later.
        String query = "INSERT INTO exams (teacher_id, title) VALUES (?, ?)";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            ps.setString(2, examTitle);
            ps.executeUpdate();
            System.out.println("✅ Exam created: " + examTitle);
        } catch (SQLException e) {
            System.out.println("❌ Failed to create exam: " + e.getMessage());
        }
    }
}



