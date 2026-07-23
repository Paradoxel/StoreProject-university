package com.storeapp.ui;

import com.storeapp.service.Logger;
import com.storeapp.service.Store;
import com.storeapp.ui.navigation.Navigation;
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
	private Navigation navigation;
	
	
	// When a coupon is valid
	private Coupon appliedCoupon;
	public CustomerPanel(
		    Store store,
		    InputValidator validator,
		    Navigation navigation
		) {
		    this.store = store;
		    this.validator = validator;
		    this.navigation = navigation;
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
		String[] options= {"1. shop","2. My Account","3. Back"};
		navigation.push("Customer");
		while(true) {
			navigation.printBreadcrumb();
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
            	navigation.pop();
                return;
			}
		}
	}
	
	// show loyal customer menu
	public void loyalCustomerMenu(LoyalCustomer lc) {
		navigation.push("Loyal Customer");
		String[] options = {
		        "1. Shop",
		        "2. My Account",
		        "3. Return an Item",
		        "4. Back"
		    };
		while(true) {
			navigation.printBreadcrumb();
			validator.printBox("LOYAL CUSTOMER MENU", options);
			int choice=validator.readIntRange(1, 4);
			switch(choice) {
				case 1:shop(lc);break;
				case 2:loyalAccountMenu(lc);break;
				case 3:returnItem(lc);break;
				case 4: navigation.pop();
					return; 
			}
		}
	}
	
	// show the account info for regular customer
	private void myAccount(Customer customer) {
		navigation.push("My Account");
	    String[] options = { "1. Edit Info", "2. Purchase History", "3. Back" };
	    while (true) {
	    	navigation.printBreadcrumb();
	        validator.printBox("MY ACCOUNT", options);
	        int choice = validator.readIntRange(1, 3);
	        switch (choice) {
	            case 1: editCustomerInfo(customer); break;
	            case 2: showCustomerInvoices(customer); break;
	            case 3: navigation.pop(); 
	            	return;
	        }
	    }
	}
	
	// show the account info for loyal customer
	private void loyalAccountMenu(LoyalCustomer lc) {
		navigation.push("My Loyal Account");
	    String[] options = {
	        "1. Edit Info",
	        "2. Purchase History",
	        "3. Financial Status",
	        "4. Pay Debt",
	        "5. Renew Membership Code",
	        "6. Back"
	    };
	    while (true) {
	    	navigation.printBreadcrumb();
	        validator.printBox("LOYAL ACCOUNT", options);
	        int choice = validator.readIntRange(1, 6);
	        switch (choice) {
	            case 1: editCustomerInfo(lc); break;
	            case 2: showCustomerInvoices(lc); break;
	            case 3: viewFinancialStatus(lc); break;
	            case 4: payDebt(lc); break;
	            case 5: renewMembershipCode(lc); break;
	            case 6:
	            	navigation.pop();
	            	return;
	        }
	    }
	}
	
	
	
	public void shop(Customer customer) {
		navigation.push("Shop");
		navigation.printBreadcrumb();
		// clear old coupon
		appliedCoupon = null;
		// create a new shoping cart
		Cart cart=store.createCart(customer);
		System.out.println("🛒 Shopping cart ready.");
		// Shopping guide
		String[] guide = {
			    "Enter product CODE to add it to your cart.",
			    "Enter 'list'   to show available products.",
			    "Enter 'cart'   to view your cart.",
			    "Enter 'remove' to delete an item.",
			    "Enter 'done'   to finish and checkout."
			};
		validator.printBox("SHOPPING GUIDE", guide);
		showProducts();
		while (true) {		    
		    //  ask for command
			String input = validator.readNonEmptyString(
			        "Product code (list/cart/remove/done): "
			 );
			
		    //  check if user wants to finish
		    if (input.equalsIgnoreCase("done")) {
		        break;
		    }
		    
			 if (input.equalsIgnoreCase("list")) {
			        showProducts();
			        continue;
			  }
		    
			 
			 
			 
			 // show current cart
			// check if user wants to see current Cart
			    if (input.equalsIgnoreCase("cart")) {

			        if (cart.getItems().isEmpty()) {
			            System.out.println("🛒 Your cart is empty.");
			            continue;
			        }

			        double originalTotal = 0;
			        double discountedTotal = 0;

			        System.out.println("\n════════════════════════════════════════════════════════════════════════════");
			        System.out.println("                           🛒 YOUR SHOPPING CART");
			        System.out.println("════════════════════════════════════════════════════════════════════════════");

			        System.out.printf(
			                "%-22s %-6s %-8s %12s %8s %14s%n",
			                "Item", "Qty", "Unit", "Price", "Disc", "Subtotal");

			        System.out.println("────────────────────── ────── ──────── ─────────── ─────── ─────────────");

			        for (CartItem ci : cart.getItems()) {

			            Product p = ci.getProduct();

			            String qtyStr = (ci.getQuantity() == Math.floor(ci.getQuantity()))
			                    ? String.valueOf((long) ci.getQuantity())
			                    : String.format("%.1f", ci.getQuantity());

			            double originalSubtotal = p.getPrice() * ci.getQuantity();
			            double discountedSubtotal = ci.getTotalPrice();

			            originalTotal += originalSubtotal;
			            discountedTotal += discountedSubtotal;

			            String discount =
			                    p.getDiscountPercent() > 0
			                            ? String.format("%.0f%%", p.getDiscountPercent())
			                            : "-";

			            System.out.printf(
			                    "%-22s %-6s %-8s %,12d %8s %,14d%n",
			                    p.getName(),
			                    qtyStr,
			                    p.getUnitType(),
			                    (long) p.getPrice(),
			                    discount,
			                    (long) discountedSubtotal
			            );
			        }

			        double saved = originalTotal - discountedTotal;

			        System.out.println("────────────────────────────────────────────────────────────────────────────");

			        System.out.printf(" %-20s %,18d Tomans%n",
			                "Original Total:",
			                (long) originalTotal);

			        System.out.printf(" %-20s %,18d Tomans%n",
			                "Product Discount:",
			                (long) saved);

			        System.out.printf(" %-20s %,18d Tomans%n",
			                "Coupon Discount:",
			                0L); // Later: replace with coupon discount

			        System.out.println("────────────────────────────────────────────────────────────────────────────");

			        System.out.printf(" %-20s %,18d Tomans%n",
			                "Current Total:",
			                (long) discountedTotal);

			        System.out.println("════════════════════════════════════════════════════════════════════════════");

			        if (saved > 0) {
			            System.out.printf(
			                    "🎉 Great! You have already saved %,d Tomans on this purchase.%n",
			                    (long) saved);
			        }
			        validator.pause();

			        continue;
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
		if(cart.getItems().isEmpty()){
		    System.out.println("Cart is empty.");
		    navigation.pop();
		    return;
		}
		System.out.println("\n--- Order Summary ---");
		System.out.printf("Original  amount: %,d Tomans%n",
		        (long) cart.getTotalAmount());
		System.out.println("---------------------");
		
		
		double finalAmount = calculateFinalAmountWithCoupon(cart);

		
		// Choose payment method
		PaymentMethod method = getPaymentMethod(customer, finalAmount);
		// Execute checkout for invoice
		Invoice invoice =store.checkoutCart(cart, method,finalAmount);
		// if user have token this block execute
		if (appliedCoupon != null) {
		    appliedCoupon.incrementUsage();
		}
		
		// save to file
		store.save();
		Logger.info(
			    "Purchase completed | Customer: "
			    + customer.getName()
			    + " | Invoice: "
			    + invoice.getId()
			    + " | Amount: "
			    + invoice.getFinalAmount()
			    + " | Payment: "
			    + invoice.getPaymentMethod()
			);
		System.out.println("\n" + invoice.toString());
		System.out.println("✅ Purchase completed. Thank you!");
		navigation.pop();
	}
	
	public void showProducts() {

	    List<Product> products = store.getProducts();

	    if (products.isEmpty()) {
	        System.out.println("\n⚠️ No products available.");
	        
	        return;
	    }

	    System.out.println("\n============================== AVAILABLE PRODUCTS ==============================");
	    System.out.printf(
	            "%-10s %-20s %12s %10s %12s %8s %-8s%n",
	            "Code",
	            "Name",
	            "Price",
	            "Discount",
	            "Final Price",
	            "Stock",
	            "Unit"
	    );
	    System.out.println("-------------------------------------------------------------------------------");

	    boolean hasSellableProducts = false;

	    for (Product p : products) {
	        if (!p.isSellable()) {
	            continue;
	        }

	        hasSellableProducts = true;

	        String discount = p.getDiscountPercent() > 0
	                ? String.format("%.0f%%", p.getDiscountPercent())
	                : "-";

	        System.out.printf(
	                "%-10s %-20s %,12d %10s %,12d %8.1f %-8s%n",
	                p.getCode(),
	                p.getName(),
	                (long) p.getPrice(),
	                discount,
	                (long) p.getDiscountedPrice(),
	                p.getStock(),
	                p.getUnitType()
	        );
	    }

	    System.out.println("-------------------------------------------------------------------------------");

	    if (!hasSellableProducts) {
	        System.out.println("⚠️ No sellable products available.");
	    }

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
		navigation.push("Return Item");
		navigation.printBreadcrumb();

		System.out.println("\n--- Return an Item ---");
		String invoiceId = validator.readNonEmptyString("Enter invoice ID: ");
		Invoice inv = store.findInvoiceById(invoiceId);
		if (inv == null) {
	        System.out.println("❌ Invoice not found.");
	        navigation.pop();
	        return;
	    }
		if (!inv.getCustomer().equals(lc)) {
	        System.out.println("❌ This invoice does not belong to you.");
	        navigation.pop();
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
	    store.save();
	    Logger.info(
	    	    "Item returned | Customer: "
	    	    + lc.getName()
	    	    + " | Invoice: "
	    	    + invoiceId
	    	    + " | Product: "
	    	    + code
	    	    + " | Quantity: "
	    	    + qty
	    	);
	    // pause
	    validator.pause();
	    navigation.pop();
	}
	
	// edit customer information
	private void editCustomerInfo(Customer customer) {
		navigation.push("Edit Info");
		navigation.printBreadcrumb();
		
	    System.out.println("\n--- Edit Info ---");
	    System.out.println("(Press Enter to keep the current value)");

	    boolean updated = false;

	    String newName = validator.readOptionalString(
	            "New name (current: " + customer.getName() + "): ");

	    if (newName != null && !newName.equals(customer.getName())) {
	        customer.setName(newName);
	        updated = true;
	    }

	    String newPhone = validator.readOptionalString(
	            "New phone (current: " + customer.getPhone() + "): ");

	    if (newPhone != null && !newPhone.isEmpty()) {

	        Customer existing = store.findCustomerByPhone(newPhone);

	        if (existing != null && existing != customer) {
	            Logger.warning(
	                "Customer update failed | Duplicate phone: "
	                + newPhone
	                + " | Customer: "
	                + customer.getName()
	            );

	            System.out.println("❌ This phone number is already registered by another customer.");
	            navigation.pop();
	            return;
	        }

	        if (!newPhone.equals(customer.getPhone())) {
	            customer.setPhone(newPhone);
	            updated = true;
	        }
	    }

	    if (updated) {
	        store.save();

	        Logger.info(
	                "Customer updated: "
	                + customer.getName()
	                + " | Phone: "
	                + customer.getPhone()
	        );

	        System.out.println("✅ Customer information updated successfully.");
	    } else {
	        System.out.println("ℹ️ No changes were made.");
	    }
	    navigation.pop();
	}
	
	// show customer invoice
	public void showCustomerInvoices(Customer customer) {
		navigation.push("Purchase History");
	    navigation.printBreadcrumb();
		List<Invoice> allInvoices =store.getInvoices();
		List<Invoice> customerInvoices =new ArrayList<Invoice>();
		for(Invoice inv : allInvoices) {
			if(inv.getCustomer().equals(customer)) {
				customerInvoices.add(inv);
			}
		}
		if(customerInvoices.isEmpty()) {
			System.out.println("\n📭 No invoices found.");
			navigation.pop();
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
	    
	    navigation.pop();
	}
	
	// show status of loyal custoemr
	public void viewFinancialStatus(LoyalCustomer lc) {
		navigation.push("Financial Status");
		navigation.printBreadcrumb();
		System.out.println("\n--- Financial Status ---");
		System.out.println("Total Debt   : " + String.format("%,d Tomans", (long) lc.getDebt()));
		System.out.println("Total Credit : " + String.format("%,d Tomans", (long) lc.getCredit()));
		System.out.println("Can Buy on Credit? : " + (lc.isCreditAvailable() ? "Yes" : "No (limit reached)"));
		System.out.println("─────────────────────────────");
		// pause
		validator.pause();
		navigation.pop();
	}
	
	// pay debt for loyal customer
	public void payDebt(LoyalCustomer lc) {
		navigation.push("Pay Debt");
		navigation.printBreadcrumb();
		System.out.println("\n--- Pay Debt ---");
		System.out.println("Current debt: " + String.format("%,d Tomans", (long) lc.getDebt()));
		if(lc.getDebt()==0) {
			System.out.println("✅ No debt to pay.");
			navigation.pop();
			return;
		}
		// ask from user
		if (!validator.yesOrNo("Do you want to pay off your debt?")) {
			System.out.println("❌ Payment cancelled. Press Enter to continue...");
			navigation.pop();
			return;
		}
		Double amount = validator.readOptionalPositiveDouble("Amount to pay (or press Enter to skip): ");
		if (amount == null) {
			navigation.pop();
		    return;
		}
		if(amount>lc.getDebt()) {
			System.out.println("❌ Cannot pay more than your debt.");
			navigation.pop();
			return;
		}
		lc.payDebt(amount);
		store.save();
		Logger.info(
			    "Debt payment | Customer: "
			    + lc.getName()
			    + " | Paid: "
			    + amount
			    + " | Remaining Debt: "
			    + lc.getDebt()
			);
		System.out.println("✅ Paid " + String.format("%,.2f Tomans", amount)
        + ". Remaining debt: " + String.format("%,.2f Tomans", lc.getDebt()));
		navigation.pop();
	}
	
	
	
	// new member ship(for expiration code)
	private void renewMembershipCode(LoyalCustomer lc) {
		navigation.push("Renew Membership");
		navigation.printBreadcrumb();
		System.out.println("\n--- Renew Membership Code ---");
		System.out.println("Current code: " + lc.getMembershipCode());
		// ask for confirmation
		if(!validator.yesOrNo("Generate a new membership code?")) {
			System.out.println("❌ Renewal cancelled.");
			navigation.pop();
			return;
		}
		// Generate new code
		String newCode=store.generateMembershipCode(lc.getName());
		// keep current code 
		String oldCode = lc.getMembershipCode();
		// set the code
		lc.setMembershipCode(newCode);
		store.save();
		Logger.info(
			    "Membership code renewed | Customer: "
			    + lc.getName()
			    + " | Old: "
			    + oldCode
			    + " | New: "
			    + newCode
			);
		System.out.println("✅ New membership code: " + newCode);
		System.out.println("⚠️ Please save this code – you will need it to log in.");
		validator.pause();
		navigation.pop();
	}
	
	
	
	
	// For Coupon 
	private double calculateFinalAmountWithCoupon(Cart cart) {
		navigation.push("Apply Coupon");
	    navigation.printBreadcrumb();
		double finalAmount = cart.getTotalAmount();
		boolean hasCoupon = validator.yesOrNo("Do you have a coupon?");
		while(true) {
			if (!hasCoupon) {
				navigation.pop();
				return finalAmount;
			}
			String code = validator.readNonEmptyString("Enter coupon code: ");
			Coupon coupon = store.findCouponByCode(code);
			if (coupon==null) {
				System.out.println("❌ Coupon not found.");
			}
			else if(!coupon.isAvailable()) {
				System.out.println("❌ Coupon is expired or unavailable.");
			}
			else {
				finalAmount = coupon.applyDiscount(finalAmount);
				this.appliedCoupon=coupon;
				System.out.printf(
					    "✅ Coupon applied! New amount: %,d Tomans%n",
					    (long) finalAmount
					);
				navigation.pop();
				return finalAmount;
			}
			boolean tryAgain =validator.yesOrNo("Try another coupon?");
			if (!tryAgain) {
				navigation.pop();
	            return finalAmount;
	        }
		}
		
	}

}
