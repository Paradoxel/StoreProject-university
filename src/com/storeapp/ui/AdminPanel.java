package com.storeapp.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;
import com.storeapp.model.*;
import com.storeapp.service.*;
import com.storeapp.ui.navigation.Navigation;
import com.storeapp.util.InputValidator;

public class AdminPanel {
	
	private static final String[] ADMIN_MENU_OPTIONS = {
	        "1. Product Management",
	        "2. Coupon Management",
	        "3. Customer Management",
	        "4. Invoice Management",
	        "5. Reports",
	        "6. Back to main menu"
	};
	
	
	private final InputValidator validator;
	private final Store store;
	private final Navigation navigation;
	
	private final ProductManager productManager;
	private final CouponManager couponManager;
	private final CustomerManager customerManager;
	private final InvoiceManager invoiceManager;
	
	
	public AdminPanel(Store store, InputValidator validator, Navigation navigation) {

	    this.store = store;
	    this.validator = validator;
	    this.navigation = navigation;

	    this.productManager =
	            new ProductManager(store, validator, navigation);

	    this.couponManager =
	            new CouponManager(store, validator, navigation);

	    this.customerManager =
	            new CustomerManager(store, validator, navigation);

	    this.invoiceManager =
	            new InvoiceManager(store, validator, navigation);
	}
	
	public void showMenu() {

	    navigation.push("Admin Panel");

	    try {

	        while(true){

	            navigation.printBreadcrumb();

	            validator.printBox(
	                "ADMIN PANEL",
	                ADMIN_MENU_OPTIONS
	            );

	            int choice = validator.readIntRange(
	                1,
	                ADMIN_MENU_OPTIONS.length
	            );

	            if(!handleChoice(choice)){
	                return;
	            }
	        }

	    } finally {
	        navigation.pop();
	    }
	}
	
	private boolean handleChoice(int choice) {

		switch (choice) {
	        case 1:
	            productManager.showMenu();
	            break;
	
	        case 2:
	            couponManager.showMenu();
	            break;
	
	        case 3:
	            customerManager.showMenu();
	            break;
	
	        case 4:
	            invoiceManager.showMenu();
	            break;
	
	        case 5:
	            showReports();
	            break;
	
	        case 6:
	            return false;
    }

	    return true;
	}
	
	
	
	
	
	
	public void showDashboard() {
	    navigation.push("Dashboard");

	    try {
	        navigation.printBreadcrumb();

	        String[] stats = {
	            "Total Products  : " + getTotalProducts(),
	            "Total Customers : " + getTotalCustomers(),
	            "Total Invoices  : " + getTotalInvoices(),
	            "Total Sales     : " + formatMoney(getTotalSales()),
	            "Outstanding Debt: " + formatMoney(getTotalDebt())
	        };

	        validator.printBox("STORE DASHBOARD", stats);
	        validator.pause();

	    } finally {
	        navigation.pop();
	    }
	}
	
	private int getTotalProducts() {
	    return store.getProducts().size();
	}
	
	private int getTotalCustomers() {
	    return store.getCustomers().size();
	}
	
	private double getTotalSales() {

	    double totalSales = 0;

	    for (Invoice invoice : store.getInvoices()) {
	        totalSales += invoice.getFinalAmount();
	    }

	    return totalSales;
	}

	
	
	private int getTotalInvoices() {
	    return store.getInvoices().size();
	}
	
	private double getTotalDebt() {

	    double totalDebt = 0;

	    for (Customer customer : store.getCustomers()) {

	        if (customer instanceof LoyalCustomer loyalCustomer) {
	            totalDebt += loyalCustomer.getDebt();
	        }
	    }

	    return totalDebt;
	}
	
	private String formatMoney(double amount) {
	    return String.format("%,d Tomans", (long) amount);
	}
	
	private void showReports() {
		navigation.push("Reports");
		try {
			navigation.printBreadcrumb();
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
		    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		    // Table rows
		    for (Invoice inv : invoices) {
		    	System.out.printf("%-20s %-15s %-20s %,10d %-10s%n",
		    	        inv.getId(),
		    	        inv.getCustomer().getName(),
		    	        inv.getDateTime().format(fmt),   
		    	        (long) inv.getFinalAmount(),    
		    	        inv.getPaymentMethod());
		    }
		    // Footer
		    System.out.println("-------------------- --------------- -------------------- ---------- ----------");
		    // pause
		    
		    validator.pause();
		}
		finally {
			navigation.pop();
		}
		
	    
	}
	
}
