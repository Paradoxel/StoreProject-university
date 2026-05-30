package com.storeapp.util;

import java.util.Scanner;

/**
  A helper class to read input from the user safely.
  It asks again and again until the user gives a valid answer.
 */
public class InputValidator {

    private Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    
    // Reads an integer between min and max (both included).
    
    public int readIntRange(int min, int max) {
        while (true) {
            System.out.print("Please enter a number between " + min + " and " + max + ": ");
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                clearBuffer(); // remove the extra line after the number
                if (value >= min && value <= max) {
                    clearScreen(); // clean the screen after good input
                    return value;
                }
            } else {
                System.out.print("Invalid input. Please enter a number: ");
                discardInvalidInput(); // throw away the wrong input
            }
        }
    }

    /**
     * Reads a string that is not empty.
     * Shows a message (prompt) first, then waits for input.
     */
    public String readNonEmptyString(String prompt) {
        System.out.print(prompt);
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.print("This field cannot be empty. Please enter a value: ");
        }
    }

   
    // Prints 50 blank lines to make the screen look clean.
    public void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /**
      Removes the rest of the line after reading a number.
      This stops the next read from getting the wrong data.
     */
    private void clearBuffer() {
        scanner.nextLine();
    }

    /**
     * Reads and throws away the wrong input when the user did not type a number.
     */
    private void discardInvalidInput() {
        scanner.next();
    }
}