package com.storeapp.ui;

import com.storeapp.service.Logger;
import com.storeapp.service.Store;
import com.storeapp.util.Constants;
import com.storeapp.util.InputValidator;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
		    // check if user wants to remove an item from cart 
		    if (input.equalsIgnoreCase("remove")) {
		        String removeCode = validator.readNonEmptyString("Enter product code to remove: ");
		        
		        // Check if the product exists in the cart
		        boolean found = false;
		        for (CartItem item : cart.getItems()) {
		            if (item.getProduct().getCode().equals(removeCode)) {
		                found = true;
		                break;
		            }
		        }
		        
		        if (found) {
		            cart.removeItem(removeCode);
		            System.out.println("✅ Removed from cart.");
		        } else {
		            System.out.println("❌ This product is not in your cart.");
		        }
		        continue;
		    }
		    // check if user wants to see current Cart
		    if (input.equalsIgnoreCase("cart")) {
		    	if (cart.getItems().isEmpty()) {
		    	    System.out.println("🛒 Your cart is empty.");
		    	} else {
		    	    System.out.println("\n--- Your Cart ---");
		    	    System.out.printf(" %-24s %-6s %-8s %-12s %-14s%n", "Item", "Qty", "Unit", "Price", "Subtotal");
		    	    System.out.println(" ------------------------ ------ -------- ------------ --------------");
		    	    
		    	    for (CartItem ci : cart.getItems()) {
		    	    	String qtyStr;
		    	    	if (ci.getQuantity() == Math.floor(ci.getQuantity())) {
		    	    	    qtyStr = String.valueOf((long) ci.getQuantity());
		    	    	} else {
		    	    	    qtyStr = String.format("%.1f", ci.getQuantity());
		    	    	}
		    	        Product p = ci.getProduct();
		    	        System.out.printf(" %-24s %-6s %-8s %,12d %,14d%n",
		    	                p.getName(),
		    	                qtyStr,
		    	                p.getUnitType(),
		    	                (long) p.getDiscountedPrice(),   
		    	                (long) ci.getTotalPrice()); 
		    	    }
		    	    System.out.println(" ------------------------ ------ -------- ------------ --------------");
		    	    System.out.printf(" Total: %,d Tomans%n", (long) cart.getTotalAmount());
		    	}
		        continue;
		    }
		    
		    // Find the product
		    Product product = store.findItemByCode(input);
		    if (product == null) {
		        System.out.println("❌ Product not found.");
		        continue;
		    }
		    // Get quantity
		    System.out.print("Quantity: ");
		    double quantity = validator.readPositiveDouble();
		    // Check stock
		    if (!product.hasEnoughStock(quantity)) {
		        System.out.println("❌ Not enough stock. Available: " + product.getStock());
		        continue;
		    }
		    // Add to cart (magic of equals in cart class)
		    cart.addItem(product, quantity);
		    System.out.println("✅ " + product.getName() + " (x" + quantity + ") added to cart.");
		}
		if(cart.getItems().isEmpty()) {
			System.out.println("❌ Your cart is empty. Nothing to checkout.");
			return ;
		}
		// Choose payment method
		PaymentMethod method=getPaymentMethod(customer,cart.getTotalAmount());
		// Execute checkout for invoice
		Invoice invoice =store.checkoutCart(cart, method);
		// save to file
		store.saveToFile(Constants.STORE_FILE);
		System.out.println("\n" + invoice.toString());
		System.out.println("✅ Purchase completed. Thank you!");
	}
	
	public void showProducts() {
		List<Product> products=store.getProducts();
		if(products.isEmpty()) {
			System.out.println("\n⚠️ No products available.");
			return ;
		}
		// simple table header
		System.out.println("\n--- Available Products ---");
		System.out.printf("%-10s %-20s %11s %8s %-8s%n",
                "Code", "Name", "Price", "Stock", "Unit");
		System.out.println("---------- -------------------- ---------- -------- --------");
		for (Product p : products) {
	        if (p.isSellable()) {  // only show sellable products
	        	System.out.printf("%-10s %-20s %,11d %8.1f %-8s%n",
	        	        p.getCode(),
	        	        p.getName(),
	        	        (long) p.getPrice(),   
	        	        p.getStock(),          
	        	        p.getUnitType());
	        }
	    }
		System.out.println("---------- -------------------- ----------- -------- --------");
		
	}
	
	private PaymentMethod getPaymentMethod(Customer customer,double cartTotal) {
	    if (customer instanceof LoyalCustomer) {
        	// cast for code clean
        	LoyalCustomer lc=(LoyalCustomer)customer;
	        while (true) {
	        	// check if loyal customer can on buy credit(debt<100000)
	        	if(!lc.canBuyOnCredit(cartTotal)) {
	        		System.out.println(
	        		        "ℹ️ Your current debt + this purchase exceeds your credit limit."
	        		 );
	        		return PaymentMethod.CASH;
	        	}
	            String choice = validator.readNonEmptyString("Payment method (cash/credit): ")
	                                       .trim().toLowerCase();
	            if (choice.equals("credit")) {
	                return PaymentMethod.CREDIT;
	            } else if (choice.equals("cash")) {
	                return PaymentMethod.CASH;
	            } else {
	                System.out.println("❌ Invalid choice. Please type 'cash' or 'credit'.");
	            }
	        }
	    } else {
	        System.out.println("ℹ️ Regular customers can only pay cash.");
	        return PaymentMethod.CASH;
	    }
	}
	
	// return an item for loyal customer
	private void returnItem(LoyalCustomer lc) {
		System.out.println("\n--- Return an Item ---");
		String invoiceId = validator.readNonEmptyString("Enter invoice ID: ");
		Invoice inv = store.findInvoiceById(invoiceId);
		if (inv == null) {
	        System.out.println("❌ Invoice not found.");
	        return;
	    }
		if (!inv.getCustomer().equals(lc)) {
	        System.out.println("❌ This invoice does not belong to you.");
	        return;
	    }
		System.out.println("\nItems in this invoice:");
		for (CartItem ci : inv.getItems()) {
	        System.out.println(" - " + ci.getProduct().getCode() + " (" + ci.getProduct().getName() + ") x" + ci.getQuantity());
	    }
	    String code = validator.readNonEmptyString("Product code to return: ");
	    System.out.print("Quantity to return: ");
	    double qty = validator.readPositiveDouble();
	    store.processReturn(lc, inv, code, qty);
	    System.out.println("✅ Returned " + qty + " of " + code + ". Credit: " + lc.getCredit() + " Tomans.");
	    store.saveToFile(Constants.STORE_FILE);
	    // pause
	    validator.pause();
	}
	
	// edit info user
	private void editCustomerInfo(Customer customer) {
		System.out.println("\n--- Edit Info ---");
		System.out.println("(Press Enter to keep the current value)");
		String newName = validator.readOptionalString("New name (current: " + customer.getName() + "): ");
		String newPhone = validator.readOptionalString("New phone (current: " + customer.getPhone() + "): ");
		if (newPhone != null && !newPhone.isEmpty()) {
		    // Check if the new phone is already taken by another customer
		    Customer existing = store.findCustomerByPhone(newPhone);
		    if (existing != null && existing != customer) {
		        System.out.println("❌ This phone number is already registered by another customer.");
		        return;
		    }
		    customer.setPhone(newPhone);
		}
		store.saveToFile(Constants.STORE_FILE);
		System.out.println("✅ Info updated.");
	}
	
	// show customer invoice
	public void showCustomerInvoices(Customer customer) {
		List<Invoice> allInvoices =store.getInvoices();
		List<Invoice> customerInvoices =new ArrayList<Invoice>();
		for(Invoice inv : allInvoices) {
			if(inv.getCustomer().equals(customer)) {
				customerInvoices.add(inv);
			}
		}
		if(customerInvoices.isEmpty()) {
			System.out.println("\n📭 No invoices found.");
	        return;
		}
		System.out.println("\n--- Your Invoices ---");
		System.out.printf("%-20s %-20s %11s %-10s%n", "Invoice #", "Date", "Amount", "Payment");
		System.out.println("-------------------- -------------------- ----------- ----------");
	    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	    for (Invoice inv : customerInvoices) {
	    	System.out.printf("%-20s %-20s %,10d %-10s%n",
	    	        inv.getId(),
	    	        inv.getDateTime().format(fmt),
	    	        (long) inv.getFinalAmount(),
	    	        inv.getPaymentMethod());
	    }
	    System.out.println("-------------------- -------------------- ---------- ----------");
	    // To pause
	    validator.pause();
	}
	
	// show status of loyal custoemr
	public void viewFinancialStatus(LoyalCustomer lc) {
		System.out.println("\n--- Financial Status ---");
		System.out.println("Total Debt   : " + String.format("%,d Tomans", (long) lc.getDebt()));
		System.out.println("Total Credit : " + String.format("%,d Tomans", (long) lc.getCredit()));
		System.out.println("Can Buy on Credit? : " + (lc.isCreditAvailable() ? "Yes" : "No (limit reached)"));
		System.out.println("─────────────────────────────");
		// pause
		validator.pause();
	}
	
	// pay debt for loyal customer
	public void payDebt(LoyalCustomer lc) {
		System.out.println("\n--- Pay Debt ---");
		System.out.println("Current debt: " + String.format("%,d Tomans", (long) lc.getDebt()));
		if(lc.getDebt()==0) {
			System.out.println("✅ No debt to pay.");
			return;
		}
		// ask from user
		if (!validator.yesOrNo("Do you want to pay off your debt?")) {
			System.out.println("❌ Payment cancelled. Press Enter to continue...");
			return;
		}
		Double amount = validator.readOptionalPositiveDouble("Amount to pay (or press Enter to skip): ");
		if (amount == null) {
		    return;
		}
		if(amount>lc.getDebt()) {
			System.out.println("❌ Cannot pay more than your debt.");
			return;
		}
		lc.payDebt(amount);
		store.saveToFile(Constants.STORE_FILE);
		System.out.println("✅ Paid " + String.format("%,.2f Tomans", amount)
        + ". Remaining debt: " + String.format("%,.2f Tomans", lc.getDebt()));
	}
	
	
	
	// new member ship(for expiration code)
	private void renewMembershipCode(LoyalCustomer lc) {
		System.out.println("\n--- Renew Membership Code ---");
		System.out.println("Current code: " + lc.getMembershipCode());
		// ask for confirmation
		if(!validator.yesOrNo("Generate a new membership code?")) {
			System.out.println("❌ Renewal cancelled.");
			return;
		}
		// Generate new code
		String newCode=store.generateMembershipCode(lc.getName());
		// keep current code 
		String oldCode = lc.getMembershipCode();
		// set the code
		lc.setMembershipCode(newCode);
		store.saveToFile(Constants.STORE_FILE);
		System.out.println("✅ New membership code: " + newCode);
		System.out.println("⚠️ Please save this code – you will need it to log in.");
	}

}
