# Online Examination System (Console-Based)

## Project Overview

The **Online Examination System** is a comprehensive Java-based console application designed to facilitate secure and efficient online examinations. It provides distinct functionalities for various user roles: **Admin**, **Teachers**, and **Students**, managing the entire examination lifecycle from user authentication and quiz creation to taking exams and viewing results.

This project demonstrates core Java concepts, JDBC for database interaction, object-oriented programming principles, and modular application design.

## Features

### 🔐 User Management
* **Admin:**
    * Secure login.
    * Register and manage new Teacher and Student accounts.
    * View all registered users in the system.
    * Delete existing user accounts.
* **Teacher:**
    * Secure login and personalized dashboard.
    * View students assigned to their courses (conceptually, based on quizzes).
    * Manage and update their personal profile information.
* **Student:**
    * Secure login and personalized dashboard.
    * View announcements posted by teachers.
    * Access quizzes based on their course and availability.

### 📝 Quiz Management
* **Teacher:**
    * **Create Quizzes:** Define quiz title, associated course, teacher ID, start and end times, duration, total questions, and total marks.
    * **Question Management:** Add questions to quizzes from a central Question Bank.
    * **AI-Powered MCQ Generation:** Integrate with an AI-based MCQ generator (simulated with `DeepSeekMCQGenerator`) to easily add multiple-choice questions.
    * **Manual Question Entry:** Manually add custom MCQs to the Question Bank.
* **Student:**
    * **Take Quizzes:** Participate in timed quizzes, navigating through questions and selecting answers.
    * **Live Scoring:** Track their score as they answer questions (or upon submission).
    * **Submit Attempts:** Submit completed quiz attempts for grading.

### 📊 Result Management
* **Teacher:**
    * View comprehensive results of quizzes they have conducted.
    * Access detailed performance data for students who attempted their quizzes.
* **Student:**
    * View their personal quiz attempts with a detailed breakdown.
    * See which questions were answered correctly or incorrectly.
    * Review their overall score for each attempt.
    * **Print Results:** Generate a detailed text-based report (`.txt` file) of their quiz attempts for local record-keeping.

### 📢 Announcements
* **Teacher:** Post new announcements with messages and timestamps for their students.
* **Student:** View all relevant announcements from their teachers.

### 💾 Data Persistence
* All application data (users, quizzes, questions, quiz attempts, student answers, announcements) is stored and retrieved from a relational database using JDBC.

### ✅ Input Validation
* Includes robust input validation mechanisms to ensure data integrity and a smooth, error-free user experience within the console interface.

## Technologies Used

* **Language:** Java
* **Database:** SQL (e.g., MySQL, SQLite)
* **Database Connectivity:** JDBC (`java.sql`)
* **Date/Time API:** `java.time` (for modern date and time handling)

## Project Structure
