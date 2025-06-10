package utils;

import Questions.DeepSeekMCQGenerator;
import core.Quiz;
import Questions.QuizAttempt;
import Questions.StudentAnswer;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class FileManager {
    public static boolean printResultToFile(String studentName, Quiz quiz, QuizAttempt attempt, List<StudentAnswer> studentAnswers, Map<Integer, DeepSeekMCQGenerator.MCQ> questionsMap) {
        // Create a user-friendly file name
        String fileName = "quiz_result_" + studentName.replaceAll("\\s+", "_") + "_attempt_" + attempt.getAttemptId() + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("======= Quiz Result for " + studentName + " =======");
            writer.println("Quiz Title: " + quiz.getTitle());
            writer.println("Course: " + quiz.getCourse());
            writer.println("Attempt ID: " + attempt.getAttemptId());
            writer.println("Attempt Time: " + attempt.getAttemptStartTime());
            writer.println("Total Questions: " + quiz.getTotalQuestions());
            writer.println("Total Marks: " + quiz.getTotalMarks());
            writer.println("Your Score: " + (attempt.getScore() != -1 ? attempt.getScore() : "Not Calculated Yet"));
            writer.println("--------------------------------------------------");

            writer.println("\n--- Question-by-Question Breakdown ---");
            if (studentAnswers.isEmpty()) {
                writer.println("No answers recorded for this attempt.");
            } else {
                for (StudentAnswer answer : studentAnswers) {
                    DeepSeekMCQGenerator.MCQ question = questionsMap.get(answer.getQuestionId());
                    if (question != null) {
                        writer.println("\nQuestion ID: " + question.getId());
                        writer.println("Q: " + question.getQuestion());
                        writer.println("A) " + question.getOptionA());
                        writer.println("B) " + question.getOptionB());
                        writer.println("C) " + question.getOptionC());
                        writer.println("D) " + question.getOptionD());
                        writer.println("Your Answer: " + answer.getStudentSelectedOption());
                        writer.println("Correct Answer: " + question.getCorrectAnswer());
                        writer.println("Result: " + (answer.getIsCorrect() != null && answer.getIsCorrect() ? "Correct" : "Incorrect"));
                    } else {
                        writer.println("\nQuestion ID " + answer.getQuestionId() + " not found for this quiz.");
                    }
                }
            }
            writer.println("==================================================");
            System.out.println("✅ Result printed to file: " + fileName);
            return true;
        } catch (IOException e) {
            System.out.println("❌ Error printing result to file: " + e.getMessage());
            return false;
        }
    }
}