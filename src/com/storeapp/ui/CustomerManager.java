package com.storeapp.ui;

import com.storeapp.model.*;
import com.storeapp.service.Logger;
import com.storeapp.service.RandomDataGenerator;
import com.storeapp.service.Store;
import com.storeapp.ui.navigation.Navigation;
import com.storeapp.util.InputValidator;
import com.storeapp.util.Constants;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class CustomerManager {

    private final Store store;
    private final InputValidator validator;
    private final Navigation navigation;
    
    private static final String[] CUSTOMER_MENU_OPTIONS = {

    	    "1. Show all customers",
    	    "2. Add Loyal Customer",
    	    "3. View Customer Details",
    	    "4. Generate Sample Customers",
    	    "5. Back to Admin Menu"
    	};
    
    public CustomerManager(
            Store store,
            InputValidator validator,
            Navigation navigation) {

        this.store = store;
        this.validator = validator;
        this.navigation = navigation;
    }

    public void showMenu() {
        navigation.push("Customers");
        try {

            while(true){
                navigation.printBreadcrumb();
                validator.printBox(
                    "CUSTOMER MANAGEMENT",
                    CUSTOMER_MENU_OPTIONS
                );
                int choice =
                    validator.readIntRange(
                        1,
                        CUSTOMER_MENU_OPTIONS.length
                    );
                if(!handleChoice(choice)){
                    return;
                }
            }
        } finally {
            navigation.pop();
        }
    }
    
    private boolean handleChoice(int choice){
        switch(choice){
            case 1:
                showAllCustomers();
                break;
            case 2:
                addLoyalCustomer();
                break;
            case 3:
                viewCustomerDetails();
                break;
            case 4:
                generateSampleCustomers();
                break;
            case 5:
                return false;
        }
        return true;
    }
    
    private void addLoyalCustomer(){
        navigation.push("Add Loyal Customer");
        try {
            navigation.printBreadcrumb();
            System.out.println("\n--- Add Loyal Customer ---");
            LoyalCustomer customer =
                    createLoyalCustomer();
            System.out.println(
                "Membership Code: "
                + customer.getMembershipCode()
            );

            System.out.println(
                "Join Date: "
                + customer.getJoinDate()
            );
            System.out.println(
                "⚠️ Save this code securely."
            );
            store.addCustomer(customer);
            store.save();
            logCustomerCreated(customer);
            System.out.println(
                "✅ Customer '"
                + customer.getName()
                + "' added successfully!"
            );
        } finally {
        	validator.pause();
            navigation.pop();
        }
    }
    
    private void logCustomerCreated(LoyalCustomer customer){
        Logger.info(
            "LOYAL_CUSTOMER_CREATED | Name="
            + customer.getName()
            + " | Phone="
            + customer.getPhone()
            + " | MembershipCode="
            + customer.getMembershipCode()
        );
    }
    
    private LoyalCustomer createLoyalCustomer(){
        String name =
            validator.readNonEmptyString("Name: ");
        String phone = readUniquePhone();
        String membershipCode =
            store.generateMembershipCode(name);
        return new LoyalCustomer(
            name,
            phone,
            membershipCode,
            LocalDate.now()
        );
    }
    
    private String readUniquePhone(){
        while(true){
            String phone =
                validator.readPhoneNumber();
            if(store.findCustomerByPhone(phone)==null){
                return phone;
            }
            System.out.println(
                "❌ Phone already exists."
            );
        }
    }   
    
    private void showAllCustomers(){
        navigation.push("Show Customers");
        try{
            navigation.printBreadcrumb();
            List<Customer> customers =
                    store.getCustomers();
            if(customers.isEmpty()){
                System.out.println(
                    "\n⚠️ No customers registered."
                );
                return;
            }
            printCustomerTable(customers);
            
        } finally {
        	validator.pause();
            navigation.pop();
        }
    }
    
    
    private void printCustomerTable(List<Customer> customers){
        System.out.println("\n--- Customer List ---");
        printCustomerHeader();
        int index = 1;
        for(Customer customer : customers){
            printCustomerRow(customer, index);
            index++;
        }
        printCustomerFooter();
    }


    private void printCustomerHeader(){
        System.out.printf(
            "%-4s %-20s %-15s %-10s %-15s %10s %10s%n",
            "#",
            "Name",
            "Phone",
            "Type",
            "Code",
            "Debt",
            "Credit"
        );
        System.out.println(
            "---- -------------------- --------------- ---------- --------------- ---------- ----------"
        );
    }


    private void printCustomerRow(Customer customer, int index){
        String type =
                (customer instanceof LoyalCustomer)
                ? "Loyal"
                : "Regular";
        String membershipCode = "";
        double debt = 0.0;
        double credit = 0.0;
        if(customer instanceof LoyalCustomer loyalCustomer){
            membershipCode =
                    loyalCustomer.getMembershipCode();
            debt =
                    loyalCustomer.getDebt();
            credit =
                    loyalCustomer.getCredit();
        }

        System.out.printf(
            "%-4d %-20s %-15s %-10s %-15s %10.2f %10.2f%n",
            index,
            customer.getName(),
            customer.getPhone(),
            type,
            membershipCode,
            debt,
            credit
        );
    }


    private void printCustomerFooter(){
        System.out.println(
            "---- -------------------- --------------- ---------- --------------- ---------- ----------"
        );
    }
    
    
    private void viewCustomerDetails(){
        navigation.push("Customer Details");
        try {
            navigation.printBreadcrumb();
            System.out.println(
                "\n--- View Customer Details ---"
            );
            String input =
                validator.readNonEmptyString(
                    "Enter phone number or membership code: "
                );
            Customer customer =
                    findCustomer(input);
            if(customer == null){
                System.out.println(
                    "❌ No customer found with this phone number or membership code."
                );
                return;
            }
            printCustomerDetails(customer);
            
        } finally {
        	validator.pause();
            navigation.pop();
        }
    }
    
    private Customer findCustomer(String input){
        Customer customer =
                store.findLoyalCustomerByCode(input);
        if(customer == null){
            customer =
                store.findCustomerByPhone(input);
        }
        return customer;
    }
    
    private void printCustomerDetails(Customer customer){
        System.out.println(
            "\n📋 Customer Details:"
        );
        System.out.println(
            "Name  : "
            + customer.getName()
        );
        System.out.println(
            "Phone : "
            + customer.getPhone()
        );
        System.out.println(
            "Type  : "
            + (customer instanceof LoyalCustomer
                ? "Loyal"
                : "Regular")
        );
        if(customer instanceof LoyalCustomer loyalCustomer){
            System.out.println(
                "Member Code : "
                + loyalCustomer.getMembershipCode()
            );
            System.out.println(
                "Join Date   : "
                + loyalCustomer.getJoinDate()
            );
            System.out.println(
                "Debt (owed) : "
                + String.format(
                    "%.2f Tomans",
                    loyalCustomer.getDebt()
                )
            );
            System.out.println(
                "Credit      : "
                + String.format(
                    "%.2f Tomans",
                    loyalCustomer.getCredit()
                )
            );
        }
        System.out.println(
            "─────────────────────────────"
        );
    }
       
    private void generateSampleCustomers(){
        navigation.push("Generate Samples");
        try{
            navigation.printBreadcrumb();
            System.out.print(
                "How many random customers? (1-10)\n"
            );
            int count =
                validator.readIntRange(1,10);
            RandomDataGenerator generator =
                new RandomDataGenerator(store);
            generator.generateCustomers(count);
            store.save();
            Logger.info(
                "SAMPLE_CUSTOMERS_GENERATED | Count="
                + count
            );
            System.out.println(
                "✅ "
                + count
                + " sample customers generated and saved!"
            );
            validator.pause();
        } finally {
            navigation.pop();
        }
    }
}