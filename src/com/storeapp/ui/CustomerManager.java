package com.storeapp.ui;

import com.storeapp.model.*;
import com.storeapp.service.Store;
import com.storeapp.util.InputValidator;
import com.storeapp.util.Constants;
import java.io.IOException;
import java.time.LocalDate;

public class CustomerManager {

    private Store store;
    private InputValidator validator;

    public CustomerManager(Store store, InputValidator validator) {
        this.store = store;
        this.validator = validator;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n--- Customer Management ---");
            System.out.println("1. Add Loyal Customer");
            System.out.println("2. Back to Admin Menu");

            int choice = validator.readIntRange(1, 2);

            switch (choice) {
                case 1:
                    addLoyalCustomer();
                    break;
                case 2:
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
}