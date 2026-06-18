package com.storeapp.ui;

import com.storeapp.model.*;
import com.storeapp.service.Logger;
import com.storeapp.service.RandomDataGenerator;
import com.storeapp.service.Store;
import com.storeapp.util.InputValidator;
import com.storeapp.util.Constants;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class CustomerManager {

    private Store store;
    private InputValidator validator;

    public CustomerManager(Store store, InputValidator validator) {
        this.store = store;
        this.validator = validator;
    }

    public void showMenu() {
    	String[] options = {
    		    "1. Show all customers",
    		    "2. Add Loyal Customer",
    		    "3. View Customer Details",
    		    "4. Generate Sample Customers",   
    		    "5. Back to Admin Menu"
    		};

        while (true) {
            validator.printBox("CUSTOMER MANAGEMENT", options);

            int choice = validator.readIntRange(1, 5);

            switch (choice) {
                case 1: showAllCustomers(); break;
                case 2: addLoyalCustomer(); break;
                case 3: viewCustomerDetails(); break;
                case 4: generateSampleCustomers(); break;
                case 5:
                    return;
            }
        }
    }

    private void addLoyalCustomer() {
        System.out.println("\n--- Add Loyal Customer ---");
        String name = validator.readNonEmptyString("Name: ");
        String phone;
        while (true) {
        	phone = validator.readPhoneNumber();
            if (store.findCustomerByPhone(phone) == null) {
                break;
            }
            System.out.println("❌ This phone number is already registered. Please enter a different one.");
        }
        String membershipCode = store.generateMembershipCode(name);
        LocalDate joinDate = LocalDate.now();
        System.out.println("Membership Code (auto-generated): " + membershipCode);
        System.out.println("Join Date (today): " + joinDate);
        System.out.println("⚠️ Please save this code securely – the customer will need it to log in.");
        LoyalCustomer loyalCustomer = new LoyalCustomer(name, phone, membershipCode, joinDate);
        store.addCustomer(loyalCustomer);
        try {
            store.saveToFile(Constants.STORE_FILE);
            System.out.println("✅ Loyal customer '" + loyalCustomer.getName() + "' added successfully!");
            Logger.log("Loyal customer created by admin: " + name + " | Code: " + membershipCode);
        } catch (IOException e) {
            System.out.println("❌ Error saving customer. Please try again.");
            Logger.log("ERROR: Failed to save customer: " + e.getMessage());
        }
    }
    
    
    private void showAllCustomers() {
        List<Customer> customers = store.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("\n⚠️ No customers registered.");
            return;
        }

        // Print table header
        System.out.println("\n--- Customer List ---");
        System.out.printf("%-4s %-20s %-15s %-10s %-10s %-10s %-10s%n",
                          "#", "Name", "Phone", "Type", "Code", "Debt", "Credit");
        System.out.println("---- -------------------- --------------- ---------- ---------- ---------- ----------");

        int index = 1;
        for (Customer c : customers) {
            // Determine customer type and optional fields
            String type = (c instanceof LoyalCustomer) ? "Loyal" : "Regular";
            String memberCode = "";
            double debt = 0.0;
            double credit = 0.0;

            // If loyal, get extra details
            if (c instanceof LoyalCustomer lc) {
                memberCode = lc.getMembershipCode();
                debt = lc.getDebt();
                credit = lc.getCredit();
            }

            // Print one row
            System.out.printf("%-4d %-20s %-15s %-10s %-10s %10.2f %10.2f%n",
                    index,
                    c.getName(),
                    c.getPhone(),
                    type,
                    memberCode,
                    debt,
                    credit);
            index++;
        }
        // Footer line
        System.out.println("---- -------------------- --------------- ---------- ---------- ---------- ----------");
        // for pause
        validator.pause();
    }
    
    
    private void viewCustomerDetails() {
        System.out.println("\n--- View Customer Details ---");
        String input = validator.readNonEmptyString("Enter phone number or membership code: ");
        
        // Try to find the customer – first as loyal by code, then by phone
        Customer c = store.findLoyalCustomerByCode(input);
        if (c == null) {
            c = store.findCustomerByPhone(input);
        }
        
        if (c == null) {
            System.out.println("❌ No customer found with this phone number or membership code.");
            return;
        }

        // Basic info
        System.out.println("\n📋 Customer Details:");
        System.out.println("Name  : " + c.getName());
        System.out.println("Phone : " + c.getPhone());
        System.out.println("Type  : " + (c instanceof LoyalCustomer ? "Loyal" : "Regular"));

        // Loyal customer extra details
        if (c instanceof LoyalCustomer) {
        	LoyalCustomer lc = (LoyalCustomer) c; 
            System.out.println("Member Code : " + lc.getMembershipCode());
            System.out.println("Join Date   : " + lc.getJoinDate());
            System.out.println("Debt (owed) : " + String.format("%.2f Tomans", lc.getDebt()));
            System.out.println("Credit      : " + String.format("%.2f Tomans", lc.getCredit()));
        }
        System.out.println("─────────────────────────────");
        // pause
        validator.pause();
    }
    
    
    private void generateSampleCustomers() {
        System.out.print("How many random customers? (1-10)\n");
        int count = validator.readIntRange(1, 10);
        RandomDataGenerator gen = new RandomDataGenerator(store);
        gen.generateCustomers(count);
        try {
            store.saveToFile(Constants.STORE_FILE);
            System.out.println("✅ " + count + " sample customers generated and saved!");
        } catch (IOException e) {
            System.out.println("❌ Customers generated but could not save.");
        }
    }
    
    
    
    
    
    
    
    
    
    
}