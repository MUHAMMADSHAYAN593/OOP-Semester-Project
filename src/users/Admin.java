package users;

import db.Con;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Admin {
    private final String adminUsername = "admin";
    private final String adminPassword = "admin123";

    public boolean login(String username, String password) {
        return adminUsername.equals(username) && adminPassword.equals(password);
    }

    public void registerTeacher(String id, String name, String pass, String course) {
        String query = "INSERT INTO teachers (id, name, password, course) VALUES (?, ?, ?, ?)";

        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, pass);
            ps.setString(4, course);
            ps.executeUpdate();
            System.out.println("✅ Teacher Registered: " + name + " (Course: " + course + ")");
        } catch (SQLException e) {
            System.out.println("❌ Failed to register teacher: " + e.getMessage());
        }
    }



    public void registerStudent(String id, String name, String pass, String assignedCourse) {
        String query = "INSERT INTO students (id, name, password, course) VALUES (?, ?, ?, ?)";

        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, pass);
            ps.setString(4, assignedCourse);
            ps.executeUpdate();
            System.out.println("✅ Student Registered: " + name + " (Course: " + assignedCourse + ")");
        } catch (SQLException e) {
            System.out.println("❌ Failed to register student: " + e.getMessage());
        }
    }




    public List<Teacher> getAllTeachers() {
        List<Teacher> list = new ArrayList<>();
        String query = "SELECT * FROM teachers";

        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            var rs = ps.executeQuery();
            while (rs.next()) {
                Teacher t = new Teacher(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("course")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to fetch teachers: " + e.getMessage());
        }

        return list;
    }


    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        String query = "SELECT * FROM students";

        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            var rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String pass = rs.getString("password");
                String course = rs.getString("course");
                studentList.add(new Student(id, name, pass, course));
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to fetch students: " + e.getMessage());
        }

        return studentList;
    }


    // --- MODIFIED deleteTeacherById method ---
    public void deleteTeacherById(String teacherId) {
        Connection con = null;
        try {
            con = Con.getConnection();
            con.setAutoCommit(false); // Start transaction

            // 1. Delete associated announcements first
            String deleteAnnouncementsSql = "DELETE FROM announcements WHERE teacher_id = ?";
            try (PreparedStatement psAnnouncements = con.prepareStatement(deleteAnnouncementsSql)) {
                psAnnouncements.setString(1, teacherId);
                int deletedAnnouncements = psAnnouncements.executeUpdate();
                System.out.println("DEBUG: Deleted " + deletedAnnouncements + " announcements for teacher ID: " + teacherId);
            }

            // 2. Delete associated quizzes (if quizzes table is linked to teachers)
            //    You might need to add code here to delete related quiz_questions or student_answers first
            //    if quizzes have foreign keys from other tables.
            //    Example (assuming quizzes is linked to teacher_id):
            //    String deleteQuizzesSql = "DELETE FROM quizzes WHERE teacher_id = ?";
            //    try (PreparedStatement psQuizzes = con.prepareStatement(deleteQuizzesSql)) {
            //        psQuizzes.setString(1, teacherId);
            //        int deletedQuizzes = psQuizzes.executeUpdate();
            //        System.out.println("DEBUG: Deleted " + deletedQuizzes + " quizzes for teacher ID: " + teacherId);
            //    }

            // 3. Delete the teacher itself
            String deleteTeacherSql = "DELETE FROM teachers WHERE id = ?";
            try (PreparedStatement psTeacher = con.prepareStatement(deleteTeacherSql)) {
                psTeacher.setString(1, teacherId);
                int rowsAffected = psTeacher.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✅ Teacher (ID: " + teacherId + ") and associated data deleted successfully!");
                    con.commit(); // Commit transaction if successful
                } else {
                    System.out.println("❌ Teacher with ID: " + teacherId + " not found.");
                    con.rollback(); // Rollback if teacher not found
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to delete teacher: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback(); // Rollback transaction on error
                    System.out.println("Transaction rolled back.");
                } catch (SQLException ex) {
                    System.out.println("❌ Error during rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true); // Restore auto-commit mode
                    con.close();
                } catch (SQLException e) {
                    System.out.println("❌ Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    public void deleteStudentById(String studentId) {
        // Similar logic for student deletion:
        // First delete dependent records (e.g., quiz attempts, student answers)
        // Then delete the student
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection con = Con.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Student (ID: " + studentId + ") deleted successfully!");
            } else {
                System.out.println("❌ Student with ID: " + studentId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to delete student: " + e.getMessage());
        }
    }


    public Teacher getTeacherById(String id) {
        String query = "SELECT * FROM teachers WHERE id = ?";
        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return new Teacher(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("course")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching teacher: " + e.getMessage());
        }
        return null;
    }


    public Student getStudentById(String id) {
        String query = "SELECT * FROM students WHERE id = ?";

        try (Connection con = Con.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            var rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String pass = rs.getString("password");
                String course = rs.getString("course");
                return new Student(id, name, pass, course);
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to get student: " + e.getMessage());
        }
        return null;
    }




}
