package com.storeapp.ui;

import com.storeapp.service.Store;
import com.storeapp.util.InputValidator;

import java.util.List;

import com.storeapp.model.*;
public class CustomerPanel {
	private Store store;
	private InputValidator validator;
	public CustomerPanel(Store store,InputValidator validator) {
		this.store=store;
		this.validator=validator;
	}
	
	// show the menu based the customer type
	public void startPurchase(Customer customer) {
		if(customer instanceof LoyalCustomer lc) {
			loyalCustomerMenu(lc);
		}
		else {
			regularCustomerMenu(customer);
		}
	}
	
	
	// show regular  customer panel
	public void regularCustomerMenu(Customer customer) {
		String[] options= {"1. shop","2. My Account","3. Back to main menu"};
		while(true) {
			validator.printBox("CUSTOMER MENU", options);
			int choice=validator.readIntRange(1, 3);
			switch (choice) {
            case 1:
                shop(customer);
                break;
            case 2:
                myAccount(customer);
                break;
            case 3:
                return;
			}
		}
	}
	
	// show loyal customer menu
	public void loyalCustomerMenu(LoyalCustomer lc) {
		String[] options = {
		        "1. Shop",
		        "2. My Account",
		        "3. Return an Item",
		        "4. Back to main menu"
		    };
		while(true) {
			validator.printBox("LOYAL CUSTOMER MENU", options);
			int choice=validator.readIntRange(1, 4);
			switch(choice) {
				case 1:shop(lc);break;
				case 2:loyalAccountMenu(lc);break;
				case 3:returnItem(lc);break;
				case 4:return; 
			}
		}
	}
	
	// show the account info for regular customer
	private void myAccount(Customer customer) {
	    String[] options = { "1. Edit Info", "2. Purchase History", "3. Back" };
	    while (true) {
	        validator.printBox("MY ACCOUNT", options);
	        int choice = validator.readIntRange(1, 3);
	        switch (choice) {
	            case 1: editCustomerInfo(customer); break;
	            case 2: showCustomerInvoices(customer); break;
	            case 3: return;
	        }
	    }
	}
	
	// show the account info for loyal customer
	private void loyalAccountMenu(LoyalCustomer lc) {
	    String[] options = {
	        "1. Edit Info",
	        "2. Purchase History",
	        "3. Financial Status",
	        "4. Pay Debt",
	        "5. Renew Membership Code",
	        "6. Back"
	    };
	    while (true) {
	        validator.printBox("LOYAL ACCOUNT", options);
	        int choice = validator.readIntRange(1, 6);
	        switch (choice) {
	            case 1: editCustomerInfo(lc); break;
	            case 2: showCustomerInvoices(lc); break;
	            case 3: viewFinancialStatus(lc); break;
	            case 4: payDebt(lc); break;
	            case 5: renewMembershipCode(lc); break;
	            case 6: return;
	        }
	    }
	}
	
	
	
	public void shop(Customer customer) {
		// create a new shoping cart
		Cart cart=store.createCart(customer);
		System.out.println("🛒 Shopping cart ready.");
		// Shopping guide
		String[] guide = {
		    "Enter product CODE to add it to your cart.",
		    "Enter 'done'  to finish and checkout.",
		    "Enter 'cart'  to view your cart.",
		    "Enter 'remove' to delete an item."
		};
		validator.printBox("SHOPPING GUIDE", guide);
		while (true) {
		    // 1. show available products
		    showProducts();
		    
		    // 2. ask for command
		    String input = validator.readNonEmptyString("Enter product code (or 'done' to finish): ");
		    
		    // 3. check if user wants to finish
		    if (input.equalsIgnoreCase("done")) {
		        break;
		    }
		    
		    // 4handle adding product (next steps)
		}

	}
	
	public void showProducts() {
		List<Product> products=store.getProducts();
		if(products.isEmpty()) {
			System.out.println("\n⚠️ No products available.");
			return ;
		}
		// simple table header
		System.out.println("\n--- Available Products ---");
		System.out.printf("%-10s %-20s %10s %8s %-8s%n",
                "Code", "Name", "Price", "Stock", "Unit");
		System.out.println("---------- -------------------- ---------- -------- --------");
		for (Product p : products) {
	        if (p.isSellable()) {  // only show sellable products
	            System.out.printf("%-10s %-20s %10.2f %8.2f %-8s%n",
	                    p.getCode(),
	                    p.getName(),
	                    p.getPrice(),
	                    p.getStock(),
	                    p.getUnitType());
	        }
	    }
		System.out.println("---------- -------------------- ---------- -------- --------");
		
	}
	
	


}
