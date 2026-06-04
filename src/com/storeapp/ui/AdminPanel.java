package com.storeapp.ui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import com.storeapp.model.*;
import com.storeapp.service.*;
import com.storeapp.util.Constants;
import com.storeapp.util.InputValidator;

public class AdminPanel {
	private InputValidator validator;
	private Store store;
	public AdminPanel(Store store, InputValidator validator) {
	    this.store = store;
	    this.validator = validator;
	}
	
	public void showMenu() {
	    ProductManager productManager = new ProductManager(store, validator);
	    CustomerManager customerManager = new CustomerManager(store, validator);
	    String[] options = {
	    	    "1. Product Management",
	    	    "2. Customer Management",
	    	    "3. Reports",
	    	    "4. Back to main menu"
	    	};

	    while (true) {
	        validator.printBox("ADMIN PANEL", options);

	        int choice = validator.readIntRange(1, 4);

	        switch (choice) {
	        case 1:
	            productManager.showMenu();
	            break;
	        case 2:
	            customerManager.showMenu();
	            break;
	        case 3:
	            showReports();
	            break;
	        case 4:
	            return;
	        }
	    }
	}
	
	
	
	
	
	public void showDashboard() {
	    int totalProducts = store.getProducts().size();
	    int totalCustomers = store.getCustomers().size();
	    int totalInvoices = store.getInvoices().size();
	    
	    double totalSales = 0;
	    for (Invoice inv : store.getInvoices()) {
	        totalSales += inv.getFinalAmount();
	    }
	    
	    double totalDebt = 0;
	    for (Customer c : store.getCustomers()) {
	        if (c instanceof LoyalCustomer lc) {
	            totalDebt += lc.getDebt();
	        }
	    }

	    // Print a nice dashboard box
	    String[] stats = {
	        "Total Products  : " + totalProducts,
	        "Total Customers : " + totalCustomers,
	        "Total Invoices  : " + totalInvoices,
	        "Total Sales     : " + String.format("%.2f Tomans", totalSales),
	        "Outstanding Debt: " + String.format("%.2f Tomans", totalDebt)
	    };
	    validator.printBox("STORE DASHBOARD", stats);
	}
	
	
	private void showReports() {
	    List<Invoice> invoices = store.getInvoices();
	    if (invoices.isEmpty()) {
	        System.out.println("\n⚠️ No invoices yet.");
	        return;
	    }

	    // Table header
	    System.out.println("\n--- Invoice History ---");
	    System.out.printf("%-20s %-15s %-20s %-10s %-10s%n",
	                      "Invoice #", "Customer", "Date", "Amount", "Payment");
	    System.out.println("-------------------- --------------- -------------------- ---------- ----------");

	    // Table rows
	    for (Invoice inv : invoices) {
	        System.out.printf("%-20s %-15s %-20s %10.2f %-10s%n",
	                inv.getId(),
	                inv.getCustomer().getName(),
	                inv.getDateTime().toString(),
	                inv.getFinalAmount(),
	                inv.getPaymentMethod());
	    }
	    // Footer
	    System.out.println("-------------------- --------------- -------------------- ---------- ----------");
	}
	
}
