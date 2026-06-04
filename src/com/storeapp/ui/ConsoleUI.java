package com.storeapp.ui;

import java.io.IOException;
import java.util.Scanner;
import com.storeapp.model.Customer;
import com.storeapp.model.LoyalCustomer;
import com.storeapp.service.Store;
import com.storeapp.util.Constants;
import com.storeapp.util.InputValidator;

/**
 * Main entry point for the console application.
 * Manages the main menu, login, and sign-up flows.
 */
public class ConsoleUI {

    // Core dependencies
    private Store store;
    private InputValidator validator;

    /**
     * Constructor: loads the store from file and sets up the validator.
     */
    public ConsoleUI(Scanner scanner) {
        this.validator = new InputValidator(scanner);
        try {
            this.store = Store.loadFromFile(Constants.STORE_FILE);
        } catch (ClassNotFoundException | IOException e) {
            this.store = new Store();
        }
    }



    /**
     * Starts the main application loop.
     */
    public void start() {
        System.out.println("🎉 Welcome to the Store Management System!");

        String[] options = {
            "1. Login",
            "2. Sign Up",
            "3. Exit"
        };

        while (true) {
            validator.printBox("MAIN MENU", options);

            int choice = validator.readIntRange(1, 3);

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    signUp();
                    break;
                case 3:
                	validator.printTitle("👋 Goodbye!");
                    return;
            }
        }
    }

    // --- Authentication & Registration ---

    /**
     * Handles login for both admin and regular customers.
     */
    public void login() {
    	validator.printTitle("LOGIN");
        String userCode = validator.readNonEmptyString("Please enter your code or phone number: ");

        // Check for admin login
        
        if (userCode.equals(Constants.ADMIN_CODE)) {
        	
            AdminPanel adminPanel = new AdminPanel(store, validator);
            adminPanel.showMenu();
            return;
        }
        // check loyal
        Customer loyalCustomer = store.findLoyalCustomerByCode(userCode);
        if (loyalCustomer != null) {
            System.out.println("✅ Welcome back, " + loyalCustomer.getName() + " (Loyal Customer)!");
            return;
        }

        // Check for existing customer
        Customer customer = store.findCustomerByPhone(userCode);
        if (customer != null) {
        	if(customer instanceof LoyalCustomer) {
        		// A loyal customer tried to log in with phone number
                System.out.println("❌ Loyal customers must use their membership code. Please try again.");
                return;
        	}
            System.out.println("✅ Welcome back, " + customer.getName() + "!");
        } else {
            System.out.println("❌ No account found with this phone number. Please sign up first.");
        }
    }

    /**
     * Handles sign-up for new customers.
     */
    public void signUp() {
        String phone = validator.readNonEmptyString("Enter your phone number: ");

        // Check if phone number is already taken
        if (store.findCustomerByPhone(phone) != null) {
            System.out.println("❌ This phone number is already registered. Please login instead.");
            return;
        }

        String name = validator.readNonEmptyString("Enter your name: ");
        Customer newCustomer = new Customer(name, phone);
        store.addCustomer(newCustomer);
        try {
            store.saveToFile(Constants.STORE_FILE);
            System.out.println("✅ Account created successfully! Welcome, " + name + "!");
        } catch (IOException e) {
            System.out.println("Error saving customer.");
        }
        
    }



}