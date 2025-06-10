import Questions.*;
import core.Quiz;
import Validation.InputValidator;
//import core.Quiz;
import db.Con;
import users.Admin;
import users.Student;
import users.Teacher;
import Questions.DeepSeekMCQGenerator.MCQ;
import utils.FileManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Admin admin = new Admin();
        QuizDAO quizDAO = new QuizDAO();

        boolean running = true;

        while (running) {
            System.out.println("\n======= Examination System =======");
            System.out.println("0. Login as Admin");
            System.out.println("1. Login as Teacher");
            System.out.println("2. Login as Student");
            System.out.println("3. Exit");

            int choice = InputValidator.getValidatedMenuChoice(sc, "Enter choice: ");

            switch (choice) {
                case 0:
                    System.out.print("Enter Admin Username: ");
                    String adminUser = sc.nextLine();
                    System.out.print("Enter Admin Password: ");
                    String adminPass = sc.nextLine();

                    if (admin.login(adminUser, adminPass)) {
                        System.out.println("✅ Admin Logged In");
                        boolean adminRunning = true;

                        while (adminRunning) {
                            System.out.println("\n--- Admin Panel ---");
                            System.out.println("1. Register Teacher");
                            System.out.println("2. Register Student");
                            System.out.println("3. View All Teachers");
                            System.out.println("4. View All Students");
                            System.out.println("5. Logout");
                            System.out.println("6. Delete Teacher by ID");
                            System.out.println("7. Delete Student by ID");

                            int adminChoice = InputValidator.getValidatedMenuChoice(sc, "Choice: ");

                            switch (adminChoice) {
                                case 1:
                                    int tid = InputValidator.getValidatedIntInput(sc, "Enter Teacher ID (number only): ");
                                    String tname = InputValidator.getValidatedNameInput(sc, "Enter Teacher Name: ");
                                    System.out.print("Enter Password: ");
                                    String tpass = sc.nextLine();
                                    System.out.print("Enter Course Name: ");
                                    String tcourse = sc.nextLine();
                                    admin.registerTeacher(String.valueOf(tid), tname, tpass, tcourse);
                                    break;

                                case 2:
                                    int sid = InputValidator.getValidatedIntInput(sc, "Enter Student ID (number only): ");
                                    String sname = InputValidator.getValidatedNameInput(sc, "Enter Student Name: ");
                                    System.out.print("Enter Password: ");
                                    String spass = sc.nextLine();
                                    System.out.print("Enter Assigned Course: ");
                                    String assignedCourse = sc.nextLine();
                                    admin.registerStudent(String.valueOf(sid), sname, spass, assignedCourse);
                                    break;

                                case 3:
                                    System.out.println("📋 All Registered Teachers:");
                                    admin.getAllTeachers().forEach(t ->
                                            System.out.println(" - " + t.getName() + " (ID: " + t.getId() + ", Course: " + t.getCourse() + ")"));
                                    break;

                                case 4:
                                    System.out.println("📋 All Registered Students:");
                                    admin.getAllStudents().forEach(s ->
                                            System.out.println(" - " + s.getName() + " (ID: " + s.getId() + ", Course: " + s.getCourse() + ")"));
                                    break;

                                case 5:
                                    adminRunning = false;
                                    System.out.println("🔒 Admin Logged Out.");
                                    break;

                                case 6:
                                    System.out.print("Enter Teacher ID to delete: ");
                                    String delTid = sc.nextLine();
                                    admin.deleteTeacherById(delTid);
                                    break;

                                case 7:
                                    System.out.print("Enter Student ID to delete: ");
                                    String delSid = sc.nextLine();
                                    admin.deleteStudentById(delSid);
                                    break;

                                default:
                                    System.out.println("⚠️ Invalid choice. Try again.");
                            }
                        }
                    } else {
                        System.out.println("❌ Invalid Admin Credentials");
                    }
                    break;

                case 1:
                    System.out.print("Enter Teacher ID: ");
                    String tid = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String tpass = sc.nextLine();

                    Teacher teacher = admin.getTeacherById(tid);
                    if (teacher != null && teacher.getPassword().equals(tpass)) {
                        System.out.println("✅ Teacher Logged In");
                        boolean teacherRunning = true;

                        while (teacherRunning) {
                            System.out.println("\n--- Teacher Panel ---");
                            System.out.println("1. View Assigned Students");
                            System.out.println("2. Edit Profile");
                            System.out.println("3. View Own Info");
                            System.out.println("4. Add Announcement");
                            System.out.println("5. Set Exam / Quiz");
                            System.out.println("6. Manage Quiz");
                            System.out.println("7. See Results");
                            System.out.println("8. Update Results");
                            System.out.println("9. Logout");
                            int tChoice = InputValidator.getValidatedIntInput(sc, "Choice: ");

                            switch (tChoice) {
                                case 1:
                                    List<String> assigned = teacher.getAssignedStudents();
                                    if (assigned.isEmpty()) {
                                        System.out.println("No students assigned.");
                                    } else {
                                        System.out.println("📋 Assigned Students:");
                                        assigned.forEach(s -> System.out.println(" - " + s));
                                    }
                                    break;


                                case 2:
                                    System.out.print("Enter new name: ");
                                    String newName = sc.nextLine();
                                    System.out.print("Enter new course: ");
                                    String newCourse = sc.nextLine();
                                    teacher.editProfile(newName, newCourse);
                                    break;

                                case 3:
                                    teacher.viewProfile();
                                    break;

                                case 4:
                                    System.out.print("Enter announcement message: ");
                                    String announcementMessage = sc.nextLine();

                                    // Prompt for date and time
                                    System.out.println("Enter announcement date and time (YYYY-MM-DD HH:MM:SS):");
                                    String dateTimeString = sc.nextLine();

                                    try {
                                        // Parse the string into LocalDateTime, then convert to Timestamp
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
                                        Timestamp announcementTimestamp = Timestamp.valueOf(localDateTime);

                                        teacher.addAnnouncement(announcementMessage, announcementTimestamp);
                                    } catch (DateTimeParseException e) {
                                        System.out.println("❌ Invalid date/time format. Please use YYYY-MM-DD HH:MM:SS.");
                                        System.out.println("Announcement not posted with date/time.");
                                    }
                                    break;
                                case 5:
                                    System.out.println("\n--- Create New Quiz ---");
                                    System.out.print("Enter Quiz Title: ");
                                    String quizTitle = sc.nextLine();

                                    Timestamp quizStartTime = null;
                                    while (quizStartTime == null) {
                                        System.out.print("Enter Start Time (YYYY-MM-DD HH:MM:SS): ");
                                        String startStr = sc.nextLine();
                                        try {
                                            quizStartTime = Timestamp.valueOf(LocalDateTime.parse(startStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                                        } catch (DateTimeParseException e) {
                                            System.out.println("❌ Invalid start time format. Use YYYY-MM-DD HH:MM:SS.");
                                        }
                                    }

                                    Timestamp quizEndTime = null;
                                    while (quizEndTime == null) {
                                        System.out.print("Enter End Time (YYYY-MM-DD HH:MM:SS): ");
                                        String endStr = sc.nextLine();
                                        try {
                                            quizEndTime = Timestamp.valueOf(LocalDateTime.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                                        } catch (DateTimeParseException e) {
                                            System.out.println("❌ Invalid end time format. Use YYYY-MM-DD HH:MM:SS.");
                                        }
                                    }

                                    System.out.print("Enter Quiz Duration in Minutes: ");
                                    int duration = InputValidator.getValidatedIntInput(sc, "");

                                    System.out.print("Enter Total Number of Questions for Quiz: ");
                                    int totalQ = InputValidator.getValidatedIntInput(sc, "");

                                    System.out.print("Enter Total Marks for Quiz: ");
                                    int totalM = InputValidator.getValidatedIntInput(sc, "");

                                    teacher.createQuiz(quizTitle, teacher.getCourse(), quizStartTime, quizEndTime, duration, totalQ, totalM);
                                    break;
                                case 6:
                                    System.out.println("\n--- Take Exam ---");
                                    System.out.println("1. Use AI to Generate MCQs");
                                    System.out.println("2. Add Your Own Questions");
                                    int examOption = InputValidator.getValidatedIntInput(sc, "Choose an option: ");

                                    if (examOption == 1) {
                                        System.out.print("How many MCQs to generate? ");
                                        int count = InputValidator.getValidatedIntInput(sc, "");

                                        List<MCQ> generated = DeepSeekMCQGenerator.generateMCQs(teacher.getCourse(), count);
                                        for (MCQ mcq : generated) {
                                            System.out.println(mcq); // prints question + options
                                            System.out.print("Add this to Question Bank? (yes/no): ");
                                            if (sc.nextLine().equalsIgnoreCase("yes")) {
                                                QuestionBank.addQuestion(
                                                        teacher.getCourse(),
                                                        mcq.getQuestion(),    // Use getter
                                                        mcq.getOptionA(),     // Use getter
                                                        mcq.getOptionB(),     // Use getter
                                                        mcq.getOptionC(),     // Use getter
                                                        mcq.getOptionD(),     // Use getter
                                                        mcq.getCorrectAnswer());
                                            }
                                        }


                                    } else if (examOption == 2) { // Add Your Own Questions
                                        System.out.print("Enter Question: ");
                                        String question = sc.nextLine();
                                        System.out.print("Option A: ");
                                        String optionA = sc.nextLine();
                                        System.out.print("Option B: ");
                                        String optionB = sc.nextLine();
                                        System.out.print("Option C: ");
                                        String optionC = sc.nextLine();
                                        System.out.print("Option D: ");
                                        String optionD = sc.nextLine();
                                        System.out.print("Correct Answer (A/B/C/D): ");
                                        String correctInput = sc.nextLine().trim().toUpperCase(); // Changed variable name to avoid confusion

                                        // Validation and conversion to char
                                        if (correctInput.length() == 1 && correctInput.matches("[A-D]")) {
                                            char correctChar = correctInput.charAt(0); // Convert String to char
                                            QuestionBank.addQuestion(
                                                    teacher.getCourse(),
                                                    question,
                                                    optionA,
                                                    optionB,
                                                    optionC,
                                                    optionD,
                                                    correctChar // Pass the char
                                            );
                                            System.out.println("✅ Question added to bank."); // Added success message
                                        } else {
                                            System.out.println("❌ Invalid correct answer. Must be A, B, C, or D.");
                                            System.out.println("Question not added."); // Added message for not adding
                                        }

                                    }



                                case 7:
                                    viewTeacherQuizResults(sc, quizDAO, teacher.getId());
                                    break;

                                case 8:
                                    calculateAndUpdateQuizResults(sc, quizDAO, teacher.getId());
                                    break;

                                case 9:
                                    teacherRunning = false;
                                    System.out.println("🔒 Teacher Logged Out.");
                                    break;

                                default:
                                    System.out.println("⚠️ Invalid choice. Try again.");
                            }
                        }
                    } else {
                        System.out.println("❌ Invalid Teacher ID or Password.");
                    }
                    break;

                case 2:
                    System.out.print("Enter Student ID: ");
                    String sid = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String spass = sc.nextLine();

                    Student student = admin.getStudentById(sid);
                    if (student != null && student.getPassword().equals(spass)) {
                        System.out.println("✅ Student Logged In");
                        System.out.println("📘 Your Course: " + student.getCourse());

                        boolean studentRunning = true;
                        while (studentRunning) {
                            System.out.println("\n--- Student Dashboard ---");
                            System.out.println("1. View Announcements");
                            System.out.println("2. Take Quiz/Exam");
                            System.out.println("3. View Result");
                            System.out.println("4. Print Result");
                            System.out.println("5. Logout");

                            int sChoice = InputValidator.getValidatedMenuChoice(sc, "Choice: ");

                            switch (sChoice) {
                                case 1:
                                    boolean foundTeacher = false;
                                    for (Teacher t : admin.getAllTeachers()) {
                                        if (t.getCourse().equalsIgnoreCase(student.getCourse())) {
                                            List<String> announcements = t.getAnnouncements();
                                            if (!announcements.isEmpty()) {
                                                System.out.println("📢 Announcements:");
                                                announcements.forEach(a -> System.out.println(" - " + a));
                                            } else {
                                                System.out.println("No announcements yet.");
                                            }
                                            foundTeacher = true;
                                            break;
                                        }
                                    }
                                    if (!foundTeacher) {
                                        System.out.println("❌ No teacher found for your course.");
                                    }
                                    break;


                                case 2: // Take Quiz
                                    System.out.println("\n--- Available Quizzes ---");
                                    List<Quiz> quizzes = student.getAvailableQuizzes();
                                    if (quizzes.isEmpty()) {
                                        System.out.println("No quizzes available for your course at this time.");
                                    } else {
                                        System.out.println("Quizzes for course: " + student.getCourse());
                                        for (int i = 0; i < quizzes.size(); i++) {
                                            Quiz q = quizzes.get(i);
                                            System.out.println((i + 1) + ". " + q.getTitle() + " (ID: " + q.getQuizId() + ")");
                                            System.out.println("   Available: " + q.getStartTime() + " to " + q.getEndTime());
                                            System.out.println("   Duration: " + q.getDurationMinutes() + " mins, Questions: " + q.getTotalQuestions() + ", Marks: " + q.getTotalMarks());
                                        }

                                        System.out.print("Enter the number of the quiz to take (0 to cancel): ");
                                        int quizChoice = InputValidator.getValidatedIntInput(sc, "");

                                        if (quizChoice > 0 && quizChoice <= quizzes.size()) {
                                            Quiz selectedQuiz = quizzes.get(quizChoice - 1);
                                            System.out.println("\nStarting Quiz: " + selectedQuiz.getTitle());

                                            int attemptId = student.startQuizAttempt(selectedQuiz.getQuizId());
                                            if (attemptId != -1) {
                                                // Fetch questions for the selected quiz
                                                List<Integer> questionIds = new ArrayList<>();
                                                String fetchQuestionsQuery = "SELECT question_id FROM quiz_questions WHERE quiz_id = ?";
                                                try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(fetchQuestionsQuery)) {
                                                    ps.setInt(1, selectedQuiz.getQuizId());
                                                    ResultSet rs = ps.executeQuery();
                                                    while (rs.next()) {
                                                        questionIds.add(rs.getInt("question_id"));
                                                    }
                                                } catch (SQLException e) {
                                                    System.out.println("❌ Error fetching quiz questions: " + e.getMessage());
                                                    break;
                                                }

                                                if (questionIds.isEmpty()) {
                                                    System.out.println("This quiz has no questions assigned to it. Please inform your teacher.");
                                                    break;
                                                }

                                                // Quiz taking loop
                                                for (int i = 0; i < questionIds.size(); i++) {
                                                    int currentQuestionId = questionIds.get(i);
                                                    DeepSeekMCQGenerator.MCQ question = QuestionBank.getQuestionById(currentQuestionId);

                                                    if (question != null) {
                                                        System.out.println("\n--- Question " + (i + 1) + " ---");
                                                        System.out.println(question.getQuestion());
                                                        System.out.println("A) " + question.getOptionA());
                                                        System.out.println("B) " + question.getOptionB());
                                                        System.out.println("C) " + question.getOptionC());
                                                        System.out.println("D) " + question.getOptionD());

                                                        String studentAnswer = "";
                                                        while (true) {
                                                            System.out.print("Your answer (A, B, C, D): ");
                                                            studentAnswer = sc.nextLine().trim().toUpperCase();
                                                            if (studentAnswer.matches("[A-D]")) {
                                                                break;
                                                            } else {
                                                                System.out.println("Invalid input. Please enter A, B, C, or D.");
                                                            }
                                                        }
                                                        student.recordStudentAnswer(attemptId, currentQuestionId, studentAnswer);
                                                    } else {
                                                        System.out.println("Could not retrieve question with ID: " + currentQuestionId);
                                                    }
                                                }
                                                student.submitQuizAttempt(attemptId);
                                            } else {
                                                System.out.println("Failed to start quiz attempt. Please try again.");
                                            }
                                        } else if (quizChoice != 0) {
                                            System.out.println("Invalid quiz number.");
                                        }
                                    }
                                    break;

                                case 3:
                                    viewStudentResults(sc, quizDAO, Integer.parseInt(student.getId()), student.getName());
                                    break;

                                case 4: // Print Result - Replace this block
                                    // Call the new method to print results to a file
                                    // Make sure 'student.getId()' is converted to String if QuizDAO expects String
                                    printStudentResult(sc, quizDAO, Integer.parseInt(student.getId()), student.getName());
                                    break;

                                case 5:
                                    studentRunning = false;
                                    System.out.println("🔒 Logged Out from Student Dashboard.");
                                    break;

                                default:
                                    System.out.println("⚠️ Invalid choice. Try again.");
                            }
                        }

                    } else {
                        System.out.println("❌ Invalid Student ID or Password.");
                    }
                    break;


                case 3:
                    running = false;
                    System.out.println("👋 Exiting... Goodbye!");
                    break;

                default:
                    System.out.println("⚠️ Invalid choice. Try again.");
            }
        }

        sc.close();
    }

    private static void viewTeacherQuizResults(Scanner scanner, QuizDAO quizDAO, String teacherId) {
        System.out.println("\n--- View Quiz Results (Teacher) ---");

        List<Quiz> quizzes = quizDAO.getQuizzesByTeacherId(teacherId);

        if (quizzes.isEmpty()) {
            System.out.println("No quizzes found for teacher ID: " + teacherId);
            return;
        }

        System.out.println("\nAvailable Quizzes for Teacher " + teacherId + ":");
        for (int i = 0; i < quizzes.size(); i++) {
            System.out.println((i + 1) + ". " + quizzes.get(i).getTitle() + " (ID: " + quizzes.get(i).getQuizId() + ")");
        }

        System.out.print("Select a quiz by number to view results (0 to go back): ");
        int quizChoice = -1;
        try {
            quizChoice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (quizChoice == 0) {
            return; // Go back
        }

        if (quizChoice < 1 || quizChoice > quizzes.size()) {
            System.out.println("Invalid quiz selection.");
            return;
        }

        Quiz selectedQuiz = quizzes.get(quizChoice - 1);
        System.out.println("\n--- Results for Quiz: " + selectedQuiz.getTitle() + " (ID: " + selectedQuiz.getQuizId() + ") ---");

        List<QuizAttempt> quizAttempts = quizDAO.getQuizAttemptsByQuizId(selectedQuiz.getQuizId());

        if (quizAttempts.isEmpty()) {
            System.out.println("No attempts found for this quiz yet.");
            return;
        }

        System.out.println("Total Attempts: " + quizAttempts.size());
        int scoredAttempts = 0;
        int totalScorePossible = selectedQuiz.getTotalQuestions(); // Assuming 1 mark per question

        for (QuizAttempt attempt : quizAttempts) {
            String scoreDisplay = (attempt.getScore() != null) ? attempt.getScore().toString() : "N/A";
            String submittedStatus = attempt.isSubmitted() ? "Submitted" : "Not Submitted";
            System.out.println("----------------------------------------");
            System.out.println("Student ID: " + attempt.getStudentId());
            System.out.println("Attempt ID: " + attempt.getAttemptId());
            System.out.println("Score: " + scoreDisplay + "/" + totalScorePossible);
            System.out.println("Status: " + submittedStatus);
            System.out.println("Attempt Start: " + attempt.getAttemptStartTime());
            if (attempt.getAttemptEndTime() != null) {
                System.out.println("Attempt End: " + attempt.getAttemptEndTime());
            }
            if (attempt.getScore() != null) {
                scoredAttempts++;
            }
        }
        System.out.println("----------------------------------------");
        System.out.println("Total Scored Attempts: " + scoredAttempts);
    }

    private static void calculateAndUpdateQuizResults(Scanner scanner, QuizDAO quizDAO, String teacherId) {
        System.out.println("\n--- Calculate and Update Quiz Results ---");

        List<Quiz> quizzes = quizDAO.getQuizzesByTeacherId(teacherId);

        if (quizzes.isEmpty()) {
            System.out.println("No quizzes found for teacher ID: " + teacherId);
            return;
        }

        System.out.println("\nAvailable Quizzes for Teacher " + teacherId + ":");
        for (int i = 0; i < quizzes.size(); i++) {
            System.out.println((i + 1) + ". " + quizzes.get(i).getTitle() + " (ID: " + quizzes.get(i).getQuizId() + ")");
        }

        System.out.print("Select a quiz by number to calculate/update results: ");
        int quizChoice = -1;
        try {
            quizChoice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (quizChoice < 1 || quizChoice > quizzes.size()) {
            System.out.println("Invalid quiz selection.");
            return;
        }

        Quiz selectedQuiz = quizzes.get(quizChoice - 1);
        System.out.println("Calculating results for Quiz: " + selectedQuiz.getTitle());

        List<DeepSeekMCQGenerator.MCQ> quizQuestions = quizDAO.getQuestionsForQuiz(selectedQuiz.getQuizId());
        if (quizQuestions.isEmpty()) {
            System.out.println("No questions found for this quiz. Cannot calculate results.");
            return;
        }

        Map<Integer, Character> correctAnswersMap = new HashMap<>();
        for (DeepSeekMCQGenerator.MCQ question : quizQuestions) {
            correctAnswersMap.put(question.getId(), question.getCorrectAnswer());
        }

        List<QuizAttempt> quizAttempts = quizDAO.getQuizAttemptsByQuizId(selectedQuiz.getQuizId());

        if (quizAttempts.isEmpty()) {
            System.out.println("No attempts found for this quiz yet.");
            return;
        }

        System.out.println("\n--- Processing Attempts ---");
        for (QuizAttempt attempt : quizAttempts) {
            // Only process if not already scored and submitted
            if (attempt.isSubmitted() && attempt.getScore() != null) {
                System.out.println("Attempt for Student " + attempt.getStudentId() + " (ID: " + attempt.getAttemptId() + ") already scored: " + attempt.getScore());
                continue;
            }

            System.out.println("Processing attempt " + attempt.getAttemptId() + " by Student " + attempt.getStudentId() + "...");

            List<StudentAnswer> studentAnswers = quizDAO.getStudentAnswersForAttempt(attempt.getAttemptId());
            int score = 0;

            for (StudentAnswer answer : studentAnswers) {
                char studentSelected = Character.toUpperCase(answer.getStudentSelectedOption());
                char correctAnswer = correctAnswersMap.getOrDefault(answer.getQuestionId(), 'X'); // Default to 'X' if question not found

                boolean isCorrect = (studentSelected == correctAnswer);
                answer.setIsCorrect(isCorrect);

                quizDAO.updateStudentAnswerCorrectStatus(answer.getAnswerId(), isCorrect);

                if (isCorrect) {
                    score++;
                }
            }

            attempt.setScore(score);
            attempt.setSubmitted(true);
            // Only set attempt end time if it's null, implying it wasn't finished by student or auto-graded
            if (attempt.getAttemptEndTime() == null) {
                attempt.setAttemptEndTime(LocalDateTime.now());
            }

            if (quizDAO.updateQuizAttempt(attempt)) {
                System.out.println("✅ Student " + attempt.getStudentId() + "'s attempt " + attempt.getAttemptId() + " scored: " + score + "/" + quizQuestions.size());
            } else {
                System.out.println("❌ Failed to update score for attempt " + attempt.getAttemptId());
            }
        }
        System.out.println("--- Result calculation complete for selected quiz ---");
    }

    /**
     * Student functionality: View their quiz results.
     * This method requires the current Student's ID to filter attempts.
     */


    // Method to view student's quiz results
    private static void viewStudentResults(Scanner scanner, QuizDAO quizDAO, int studentId, String studentName) {
        System.out.println("\n--- Your Quiz Results ---");
        List<QuizAttempt> attempts = quizDAO.getQuizAttemptsByStudentId(String.valueOf(studentId));

        if (attempts.isEmpty()) {
            System.out.println("No quiz attempts found for your account.");
            return;
        }

        System.out.println("Select an attempt to view details:");
        for (int i = 0; i < attempts.size(); i++) {
            QuizAttempt attempt = attempts.get(i);
            // Fetch quiz title for display
            Quiz quiz = quizDAO.getQuizById(attempt.getQuizId());
            String quizTitle = (quiz != null) ? quiz.getTitle() : "Unknown Quiz";

            System.out.println((i + 1) + ". Quiz: " + quizTitle +
                    ", Attempt ID: " + attempt.getAttemptId() +
                    ", Score: " + (attempt.getScore() != -1 ? attempt.getScore() : "N/A") +
                    " (Submitted: " + (attempt.isSubmitted() ? "Yes" : "No") + ")");
        }

        System.out.print("Enter attempt number to view (0 to cancel): ");
        int choice = InputValidator.getValidatedIntInput(scanner, "Enter your choice: ");

        if (choice <= 0 || choice > attempts.size()) {
            System.out.println("Invalid choice or cancelled.");
            return;
        }

        QuizAttempt selectedAttempt = attempts.get(choice - 1);
        displayDetailedQuizResults(quizDAO, selectedAttempt);
    }

    // Helper method to display detailed results of a single quiz attempt
    private static void displayDetailedQuizResults(QuizDAO quizDAO, QuizAttempt attempt) {
        System.out.println("\n--- Detailed Results for Attempt ID: " + attempt.getAttemptId() + " ---");

        Quiz quiz = quizDAO.getQuizById(attempt.getQuizId());
        if (quiz == null) {
            System.out.println("Error: Quiz details not found.");
            return;
        }

        System.out.println("Quiz Title: " + quiz.getTitle());
        System.out.println("Course: " + quiz.getCourse());
        System.out.println("Total Questions: " + quiz.getTotalQuestions());
        System.out.println("Total Marks: " + quiz.getTotalMarks());
        System.out.println("Your Score: " + (attempt.getScore() != -1 ? attempt.getScore() : "Not Calculated Yet"));
        System.out.println("Attempt Submitted: " + (attempt.isSubmitted() ? "Yes" : "No"));
        System.out.println("Attempt Time: " + attempt.getAttemptStartTime());

        List<StudentAnswer> studentAnswers = quizDAO.getStudentAnswersForAttempt(attempt.getAttemptId());
        List<DeepSeekMCQGenerator.MCQ> quizQuestions = quizDAO.getQuestionsForQuiz(quiz.getQuizId());

        Map<Integer, DeepSeekMCQGenerator.MCQ> questionsMap = new HashMap<>();
        if (quizQuestions != null) {
            for (DeepSeekMCQGenerator.MCQ q : quizQuestions) {
                questionsMap.put(q.getId(), q);
            }
        }

        System.out.println("\n--- Question-by-Question Breakdown ---");
        if (studentAnswers.isEmpty()) {
            System.out.println("No answers recorded for this attempt.");
        } else {
            for (StudentAnswer answer : studentAnswers) {
                DeepSeekMCQGenerator.MCQ question = questionsMap.get(answer.getQuestionId());
                if (question != null) {
                    System.out.println("\nQuestion ID: " + question.getId());
                    System.out.println("Q: " + question.getQuestion());
                    System.out.println("A) " + question.getOptionA());
                    System.out.println("B) " + question.getOptionB());
                    System.out.println("C) " + question.getOptionC());
                    System.out.println("D) " + question.getOptionD());
                    System.out.println("Your Answer: " + answer.getStudentSelectedOption());
                    System.out.println("Correct Answer: " + question.getCorrectAnswer());
                    System.out.println("Result: " + (answer.getIsCorrect() != null && answer.getIsCorrect() ? "✅ Correct" : "❌ Incorrect"));
                } else {
                    System.out.println("\nQuestion ID " + answer.getQuestionId() + " not found for this quiz.");
                }
            }
        }
        System.out.println("--------------------------------------------------");
    }


    // Method to print student's quiz results to a file
    private static void printStudentResult(Scanner scanner, QuizDAO quizDAO, int studentId, String studentName) {
        System.out.println("\n--- Print Quiz Result ---");
        List<QuizAttempt> attempts = quizDAO.getQuizAttemptsByStudentId(String.valueOf(studentId));

        if (attempts.isEmpty()) {
            System.out.println("No quiz attempts found to print.");
            return;
        }

        System.out.println("Select an attempt to print:");
        for (int i = 0; i < attempts.size(); i++) {
            QuizAttempt attempt = attempts.get(i);
            Quiz quiz = quizDAO.getQuizById(attempt.getQuizId());
            String quizTitle = (quiz != null) ? quiz.getTitle() : "Unknown Quiz";
            System.out.println((i + 1) + ". Quiz: " + quizTitle + ", Attempt ID: " + attempt.getAttemptId());
        }

        //System.out.print("");
        int choice = InputValidator.getValidatedIntInput(scanner, "Enter attempt number to print (0 to cancel): ");

        if (choice <= 0 || choice > attempts.size()) {
            System.out.println("Invalid choice or cancelled.");
            return;
        }

        QuizAttempt selectedAttempt = attempts.get(choice - 1);
        Quiz quiz = quizDAO.getQuizById(selectedAttempt.getQuizId());
        if (quiz == null) {
            System.out.println("Error: Could not retrieve quiz details for printing.");
            return;
        }

        List<StudentAnswer> studentAnswers = quizDAO.getStudentAnswersForAttempt(selectedAttempt.getAttemptId());
        List<DeepSeekMCQGenerator.MCQ> quizQuestions = quizDAO.getQuestionsForQuiz(quiz.getQuizId());

        Map<Integer, DeepSeekMCQGenerator.MCQ> questionsMap = new HashMap<>();
        if (quizQuestions != null) {
            for (DeepSeekMCQGenerator.MCQ q : quizQuestions) {
                questionsMap.put(q.getId(), q);
            }
        }

        // Call the FileManager to print the result to a file
        FileManager.printResultToFile(studentName, quiz, selectedAttempt, studentAnswers, questionsMap);
    }
}
