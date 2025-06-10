package Validation;

import java.util.Scanner;

public class InputValidator {

    public static int getValidatedIntInput(Scanner sc, String prompt) {
        int num;
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                num = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a valid number.");
            }
        }
        return num;
    }

    public static String getValidatedNameInput(Scanner sc, String prompt) {
        String name;
        while (true) {
            System.out.print(prompt);
            name = sc.nextLine();
            if (name.matches("[a-zA-Z ]+")) {
                break;
            } else {
                System.out.println("❌ Invalid name! Please use letters only.");
            }
        }
        return name;
    }

    public static int getValidatedMenuChoice(Scanner sc, String s) {
        int choice;
        while (true) {
            String input = sc.nextLine();
            try {
                choice = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid choice. Enter a number: ");
            }
        }
        return choice;
    }
}
