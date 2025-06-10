package Questions;

import db.Con;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank {
    // Method to add a question (assuming this is used to populate)
    public static void addQuestion(String course, String question, String optionA, String optionB, String optionC, String optionD, char correctAnswer) {
        String query = "INSERT INTO question_bank (course, question, optionA, optionB, optionC, optionD, correctAnswer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, course);
            ps.setString(2, question);
            ps.setString(3, optionA);
            ps.setString(4, optionB);
            ps.setString(5, optionC);
            ps.setString(6, optionD);
            ps.setString(7, String.valueOf(correctAnswer)); // Store char as String
            ps.executeUpdate();
            System.out.println("✅ Question added to bank.");
        } catch (SQLException e) {
            System.out.println("❌ Failed to add question to bank: " + e.getMessage());
        }
    }


    // New method to fetch all question IDs for a given course
    public static List<Integer> getQuestionIdsByCourse(String course) {
        List<Integer> questionIds = new ArrayList<>();
        String query = "SELECT id FROM question_bank WHERE course = ?";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, course);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                questionIds.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to retrieve question IDs for course " + course + ": " + e.getMessage());
        }
        return questionIds;
    }

    public static DeepSeekMCQGenerator.MCQ getQuestionById(int questionId) {
        String query = "SELECT * FROM question_bank WHERE id = ?";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, questionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String correctAnsStr = rs.getString("correctAnswer");
                char correctAnsChar = '\0'; // Default value
                if (correctAnsStr != null && !correctAnsStr.isEmpty()) {
                    correctAnsChar = correctAnsStr.charAt(0);
                }

                // Pass the char directly to the MCQ constructor
                return new DeepSeekMCQGenerator.MCQ(
                        rs.getString("question"),
                        rs.getString("optionA"),
                        rs.getString("optionB"),
                        rs.getString("optionC"),
                        rs.getString("optionD"),
                        correctAnsChar
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching question: " + e.getMessage());
        }
        return null;
    }

    public static List<DeepSeekMCQGenerator.MCQ> getAllQuestions() {
        List<DeepSeekMCQGenerator.MCQ> questions = new ArrayList<>();
        String query = "SELECT * FROM question_bank";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String correctAnsStr = rs.getString("correctAnswer");
                char correctAnsChar = '\0'; // Default value
                if (correctAnsStr != null && !correctAnsStr.isEmpty()) {
                    correctAnsChar = correctAnsStr.charAt(0);
                }
                questions.add(new DeepSeekMCQGenerator.MCQ(
                        rs.getString("question"),
                        rs.getString("optionA"),
                        rs.getString("optionB"),
                        rs.getString("optionC"),
                        rs.getString("optionD"),
                        correctAnsChar
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error retrieving all questions: " + e.getMessage());
        }
        return questions;
    }
}