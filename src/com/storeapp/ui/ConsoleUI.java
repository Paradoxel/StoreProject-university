package com.storeapp.ui;

import java.io.IOException;
import java.util.Scanner;

import com.storeapp.service.Store;
import com.storeapp.util.InputValidator;

public class ConsoleUI {
    private static final String ADMIN_CODE = "admin123";
    private static final String STORE_FILE = "store.dat";
    
    
    private Store store;
    private InputValidator validator;

    public ConsoleUI(Scanner scanner) {
    	validator=new InputValidator(scanner);
        try {
            this.store = Store.loadFromFile(STORE_FILE);
        } catch (ClassNotFoundException | IOException e) {
            this.store = new Store();
        }
    }

    public void start() {
        System.out.println("🎉 Welcome to the Store Management System!");
        while (true) {
            System.out.println("\n===== Login =====");
            String userCode = validator.readNonEmptyString("Please enter your phone number (or 'exit' to quit): ");
            if (userCode.equalsIgnoreCase("exit")) {
                System.out.println("👋 Goodbye!");
                return;
            }

            if (userCode.equals(ADMIN_CODE)) {
                System.out.println("🔑 Admin login successful. Welcome to the Admin Panel!");

            } else {
                System.out.println("🛒 Customer login. Phone number: " + userCode);

            }
        }
    }
}