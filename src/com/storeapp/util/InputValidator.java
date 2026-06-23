package com.storeapp.util;

import java.util.Scanner;

import com.storeapp.model.UnitType;

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
            System.out.print("Please choose an option between " + min + " and " + max + ": ");
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine(); // clear buffer
                if (value >= min && value <= max) {
                    return value;
                }
            } else {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.next(); // discard wrong input
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

   


    
    
    
    
    // reads a positive decimal number
    public double readPositiveDouble() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if(input.isEmpty()) return 0;
                double value = Double.parseDouble(input);

                if (value >0)
                    return value;


                System.out.print("Value must be positive: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
    
    
    
    // read unit type
    public UnitType readUnitType() {
    	System.out.print("Unit Type(COUNT, WEIGHT, VOLUME): ");
    	while(true) {
    		String unittype=scanner.nextLine().trim();
    	try {
    		UnitType unit=UnitType.valueOf(unittype.toUpperCase());
    		return unit;
    	}
    	catch (IllegalArgumentException e) {
    		System.out.print("Invalid unit. Please enter COUNT, WEIGHT, or VOLUME: ");
		}	
    	}
    }
    
    
    
    // ask yes or no 
    public boolean yesOrNo(String prompt) {
    	System.out.print(prompt+" y/n: ");
    	while(true) {
    		String answer = scanner.nextLine().trim().toLowerCase();
    		if(answer.equals("y") || answer.equals("yes")) {

    			return true;
    		}
    		else if (answer.equals("n") || answer.equals("no")) {
    			
    			return false;
    		}
    		System.out.print("Please answer y or n: ");
    	}
    }
    
    
    
    // read optional string 
    public String readOptionalString(String prompt) {
    	System.out.print(prompt);
    	String inputString=scanner.nextLine().trim();
    	if(inputString.isEmpty())
    		return null;
    	return inputString;

    }
    
    
    public double readDiscountPercent() {
        while (true) {
        	String value = readOptionalString("");

            if (value==null)
                return 0;

            try {
                double discount = Double.parseDouble(value);

                if (discount >= 0 && discount <= 100)
                    return discount;

                System.out.println("Must be between 0 and 100. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    
    public Double readOptionalDouble() {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.print("Invalid number. Please try again: ");
            return readOptionalDouble(); 
        }
    }
    
    
    public void printBox(String title, String[] options) {
        int width = 50;

        // Top border
        System.out.println("┌" + "─".repeat(width) + "┐");

        // Centered title
        int padding = (width - title.length()) / 2;
        System.out.println("│" + " ".repeat(padding) + title + " ".repeat(width - padding - title.length()) + "│");

        // Separator
        System.out.println("├" + "─".repeat(width) + "┤");

        // Options
        for (String opt : options) {
            System.out.println("│ " + opt + " ".repeat(width - opt.length() - 1) + "│");
        }

        // Bottom border
        System.out.println("└" + "─".repeat(width) + "┘");
    }
    
    //Prints a simple title inside the box 
    public void printTitle(String title) {
        int width = 50;
        String line = "─".repeat(width);
        int padding = (width - title.length()) / 2;
        
        System.out.println("┌" + line + "┐");
        System.out.println("│" + " ".repeat(padding) + title + " ".repeat(width - padding - title.length()) + "│");
        System.out.println("└" + line + "┘");
    }
    
    // pause for user 
    public void pause() {
    	System.out.println("\nPress Enter to continue...");
    	scanner.nextLine();
    }
    
    
    
    // Reads a valid phone number for iranian(Start with 09, 11 digit)
    public String readPhoneNumber() {
        while (true) {
            String phone = readNonEmptyString("Enter your phone number: ");
            
            if (!phone.matches("^09\\d{9}$")) {
                System.out.println("❌ Invalid phone number. It must start with 09 and have 11 digits.");
                continue;
            }
            
            return phone;
        }
    }
    
    
    // Reads a amount or empty
    public Double readOptionalPositiveDouble(String prompt) {
    	while(true) {
    		System.out.print(prompt);
    		String input = scanner.nextLine().trim();
    		if (input.isEmpty()) {
                return null;
            }
    		try {
                double value = Double.parseDouble(input);

                if (value > 0) {
                    return value;
                }

                System.out.println("❌ Value must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number. Try again.");
            }
    	}
    }
    
    
    
    
}