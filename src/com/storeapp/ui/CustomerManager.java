package com.storeapp.ui;

import com.storeapp.model.*;
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
            "3. Back to Admin Menu"
        };

        while (true) {
            validator.printBox("CUSTOMER MANAGEMENT", options);

            int choice = validator.readIntRange(1, 3);

            switch (choice) {
                case 1:
                    showAllCustomers();
                    break;
                case 2:
                    addLoyalCustomer();
                    break;
                case 3:
                    return;
            }
        }
    }

    private void addLoyalCustomer() {
        System.out.println("\n--- Add Loyal Customer ---");
        String name = validator.readNonEmptyString("Name: ");
        String phone;
        while (true) {
            phone = validator.readNonEmptyString("Phone: ");
            if (store.findCustomerByPhone(phone) == null) {
                break;
            }
            System.out.println("❌ This phone number is already registered. Please enter a different one.");
        }
        String membershipCode = store.generateMembershipCode(name);
        System.out.println("Membership Code (auto-generated): " + membershipCode);
        System.out.println("⚠️ Please save this code securely – the customer will need it to log in.");
        LocalDate joinDate = LocalDate.now();
        System.out.println("Join Date (today): " + joinDate);
        LoyalCustomer loyalCustomer = new LoyalCustomer(name, phone, membershipCode, joinDate);
        store.addCustomer(loyalCustomer);
        try {
            store.saveToFile(Constants.STORE_FILE);
            System.out.println("✅ Loyal customer '" + loyalCustomer.getName() + "' added successfully!");
        } catch (IOException e) {
            System.out.println("❌ Error saving customer. Please try again.");
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
    }
    
}