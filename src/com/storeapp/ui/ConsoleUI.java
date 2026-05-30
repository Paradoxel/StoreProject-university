package com.storeapp.ui;

import java.io.IOException;
import java.util.Scanner;

import com.storeapp.service.Store;

public class ConsoleUI {
    private static final String ADMIN_CODE = "admin123";
    private static final String STORE_FILE = "store.dat";
    private Store store;
    private Scanner scanner;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
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
            System.out.print("Please enter your phone number (or 'exit' to quit): ");
            String userCode = scanner.nextLine().trim();

            if (userCode.equalsIgnoreCase("exit")) {
                System.out.println("👋 Goodbye!");
                scanner.close();
                return;
            }

            System.out.println("Entered code: " + userCode);
        }
    }
}