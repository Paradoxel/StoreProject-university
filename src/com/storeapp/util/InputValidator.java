package com.storeapp.util;

import java.util.Scanner;

public class InputValidator {
	
	private Scanner scanner;
	public InputValidator(Scanner scanner) {
	    this.scanner = scanner;
	}
	
	
	public int readIntRange(int min,int max) {
		while(true) {
			if(scanner.hasNextInt()) {
				int value=scanner.nextInt();
				clearBuffer();
				if(value>=min && value<=max) {
					return value;
				}
				else {
					System.out.print("Please enter a number between " + min + " and " + max + ": ");
				}
			}
			else{
				System.out.print("Invalid input. Please enter a number: ");
				discardInvalidInput();
			}
		}
	}
	
	
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
	
	
	
	
	
	private void clearBuffer() {
	    scanner.nextLine();
	}
	
	private void discardInvalidInput() {
	    scanner.next();
	}
	
}
