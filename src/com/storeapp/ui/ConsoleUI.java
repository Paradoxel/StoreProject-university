package com.storeapp.ui;


import java.util.Scanner;
import com.storeapp.model.Customer;
import com.storeapp.model.LoyalCustomer;
import com.storeapp.service.Logger;
import com.storeapp.service.Store;
import com.storeapp.ui.navigation.Navigation;
import com.storeapp.util.Constants;
import com.storeapp.util.InputValidator;

/**
 * Main entry point for the console application.
 * Manages the main menu, login, and sign-up flows.
 */
public class ConsoleUI {

	// final elements
    private static final String[] MAIN_MENU_OPTIONS = {
    	    "1. Login",
    	    "2. Sign Up",
    	    "3. Exit"
    	};
	
	
    // Core dependencies
	private final Store store;
	private final InputValidator validator;
	private final Navigation navigation;

	public ConsoleUI(Scanner scanner) {
	    this.store = Store.loadFromFile(Constants.STORE_FILE);
	    this.validator = new InputValidator(scanner);
	    this.navigation = new Navigation();
	}


    
    // Starts the main application loop.
    public void start() {
        System.out.println("🎉 Welcome to the Store Management System!");
        navigation.push("Main Menu");
        while (true) {
        	navigation.printBreadcrumb();
        	validator.printBox("MAIN MENU", MAIN_MENU_OPTIONS);
            int choice = validator.readIntRange(1, MAIN_MENU_OPTIONS.length);

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    signUp();
                    break;
                case 3:
                	store.save();
                	navigation.clear();
                	navigation.printBreadcrumb();
                	validator.printTitle("👋 Goodbye!");
                	return;
            }
        }
    }

    // Handles user login.
    public void login() {
        validator.printTitle("LOGIN");

        String identifier =
                validator.readNonEmptyString(
                        "Please enter your code or phone number: ");

        if (loginAdmin(identifier)) {
            return;
        }

        if (loginLoyalCustomer(identifier)) {
            return;
        }

        loginRegularCustomer(identifier);
    }
    
    // Authenticates the administrator.
    private boolean loginAdmin(String identifier) {
        if (!identifier.equals(Constants.ADMIN_CODE)) {
            return false;
        }

        Logger.log("Admin logged in");
        openAdminPanel();

        return true;
    }
    
    // Authenticates a loyal customer using membership code.
    private boolean loginLoyalCustomer(String identifier) {

        Customer loyalCustomer =
                store.findLoyalCustomerByCode(identifier);

        if (loyalCustomer == null) {
            return false;
        }

        System.out.println(
                "✅ Welcome back, "
                        + loyalCustomer.getName()
                        + " (Loyal Customer)!");

        Logger.log(
                "Loyal customer logged in: "
                        + loyalCustomer.getName()
                        + " (Code: "
                        + identifier
                        + ")");

        openCustomerPanel(loyalCustomer);

        return true;
    }
    
    // Authenticates a regular customer using phone number.
    private void loginRegularCustomer(String identifier) {

        Customer customer =
                store.findCustomerByPhone(identifier);

        if (customer == null) {
            System.out.println(
                    "❌ No account found with this phone number. Please sign up first.");
            return;
        }

        if (customer instanceof LoyalCustomer) {

            System.out.println(
                    "❌ Loyal customers must use their membership code.");

            Logger.log(
                    "SECURITY: Loyal customer "
                            + customer.getName()
                            + " attempted login with phone.");

            return;
        }

        System.out.println(
                "✅ Welcome back, "
                        + customer.getName()
                        + "!");

        Logger.log(
                "Customer logged in: "
                        + customer.getName()
                        + " (Phone: "
                        + customer.getPhone()
                        + ")");

        openCustomerPanel(customer);
    }
    
    

    
    // Registers a new customer.
    public void signUp() {
        validator.printTitle("SIGN UP");

        String phone = readAvailablePhoneNumber();
        if (phone == null) {
            return;
        }

        String name = validator.readNonEmptyString("Enter your name: ");

        createCustomer(name, phone);
        
    }
    
    
    
    // Reads a phone number and checks if it is already registered.
    private String readAvailablePhoneNumber() {
        while (true) {
            String phone = validator.readPhoneNumber();

            if (store.findCustomerByPhone(phone) == null) {
                return phone;
            }

            System.out.println("❌ This phone number is already registered.");

            if (!validator.yesOrNo("Try another phone number?")) {
                return null;
            }
        }
    }
    
    // Creates a new customer.
    private void createCustomer(String name, String phone) {
        Customer customer = new Customer(name, phone);

        store.addCustomer(customer);
        store.save();

        System.out.println(
        	    "✅ Account created successfully! Welcome, "
        	            + customer.getName()
        	            + "!");
        Logger.log(
        	    "New customer signed up: "
        	            + customer.getName()
        	            + " ("
        	            + customer.getPhone()
        	            + ")");
    }
    
    

    
    // Opens the customer panel.
    private void openCustomerPanel(Customer customer) {
        CustomerPanel panel =
                new CustomerPanel(store, validator, navigation);

        panel.startPurchase(customer);
    }
    
    // Opens the administrator panel.
    private void openAdminPanel() {
        AdminPanel panel =
                new AdminPanel(store, validator, navigation);

        panel.showDashboard();
        panel.showMenu();
    }
    
}