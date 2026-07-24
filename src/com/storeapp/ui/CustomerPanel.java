package com.storeapp.ui;

import com.storeapp.service.Logger;
import com.storeapp.service.Store;
import com.storeapp.ui.navigation.Navigation;
import com.storeapp.util.InputValidator;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.storeapp.model.*;
public class CustomerPanel {
	
	private final Store store;
	private final InputValidator validator;
	private final Navigation navigation;
	
	private static final String[] CUSTOMER_MENU_OPTIONS = {
	        "1. Shop",
	        "2. My Account",
	        "3. Back"
	};

	private static final String[] LOYAL_CUSTOMER_MENU_OPTIONS = {
	        "1. Shop",
	        "2. My Account",
	        "3. Return an Item",
	        "4. Back"
	};

	private static final String[] MY_ACCOUNT_OPTIONS = {
	        "1. Edit Info",
	        "2. Purchase History",
	        "3. Back"
	};

	private static final String[] LOYAL_ACCOUNT_OPTIONS = {
	        "1. Edit Info",
	        "2. Purchase History",
	        "3. Financial Status",
	        "4. Pay Debt",
	        "5. Renew Membership Code",
	        "6. Back"
	};

	private static final String[] SHOPPING_GUIDE = {
	        "Enter product CODE to add it to your cart.",
	        "Enter 'list'   to show available products.",
	        "Enter 'cart'   to view your cart.",
	        "Enter 'remove' to delete an item.",
	        "Enter 'done'   to finish and checkout."
	};
	
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
		navigation.push("Customer");
		while(true) {
			navigation.printBreadcrumb();
			validator.printBox("CUSTOMER MENU", CUSTOMER_MENU_OPTIONS);
			int choice=validator.readIntRange(1, CUSTOMER_MENU_OPTIONS.length);
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
		while(true) {
			navigation.printBreadcrumb();
			validator.printBox("LOYAL CUSTOMER MENU", LOYAL_CUSTOMER_MENU_OPTIONS);
			int choice=validator.readIntRange(1, LOYAL_CUSTOMER_MENU_OPTIONS.length);
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
	    while (true) {
	    	navigation.printBreadcrumb();
	    	validator.printBox("MY ACCOUNT", MY_ACCOUNT_OPTIONS);
	        int choice = validator.readIntRange(1, MY_ACCOUNT_OPTIONS.length);
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
	    while (true) {
	    	navigation.printBreadcrumb();
	    	validator.printBox("LOYAL ACCOUNT", LOYAL_ACCOUNT_OPTIONS);
	        int choice = validator.readIntRange(1, LOYAL_ACCOUNT_OPTIONS.length);
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

	    appliedCoupon = null;

	    Cart cart = store.createCart(customer);

	    validator.printBox(
	        "SHOPPING GUIDE",
	        SHOPPING_GUIDE
	    );

	    showProducts();


	    while(true) {

	        String input =
	            validator.readNonEmptyString(
	                "Product code (list/cart/remove/done): "
	            );


	        if(input.equalsIgnoreCase("done"))
	            break;


	        if(handleShopCommand(input, cart))
	            continue;


	        addProductToCart(input, cart);
	    }


	    checkout(customer, cart);

	    navigation.pop();
	}

		
	private boolean handleShopCommand(String input, Cart cart) {

	    switch (input.toLowerCase()) {

	        case "list":
	            showProducts();
	            return true;

	        case "cart":
	            showCart(cart);
	            return true;

	        case "remove":
	            removeFromCart(cart);
	            return true;

	        default:
	            return false;
	    }
	}
		
	private void addProductToCart(String code, Cart cart) {

		
		

		

	    Product product = store.findItemByCode(code);

	    if(product == null) {
	        System.out.println("❌ Product not found.");
	        return;
	    }


	    System.out.print("Quantity: ");

	    double quantity = validator.readPositiveDouble();


	    if(!product.hasEnoughStock(quantity)) {

	        System.out.println(
	            "❌ Not enough stock. Available: "
	            + product.getStock()
	        );

	        return;
	    }


	    cart.addItem(product, quantity);

	    System.out.println(
	        "✅ " 
	        + product.getName()
	        + " (x"
	        + quantity
	        + ") added to cart."
	    );
	}
		
	
	private void removeFromCart(Cart cart) {

	    String code = validator.readNonEmptyString(
	            "Enter product code to remove: "
	    );

	    boolean exists = cart.getItems()
	            .stream()
	            .anyMatch(item ->
	                    item.getProduct()
	                        .getCode()
	                        .equals(code)
	            );

	    if(exists) {
	        cart.removeItem(code);
	        System.out.println("✅ Removed from cart.");
	    }
	    else {
	        System.out.println("❌ This product is not in your cart.");
	    }
	}

	private void showProducts() {

	    List<Product> products = store.getProducts();

	    if (products.isEmpty()) {
	        System.out.println("\n⚠️ No products available.");
	        return;
	    }

	    printProductHeader();

	    boolean hasSellableProducts = printProductList(products);

	    printProductFooter(hasSellableProducts);
	}
	
	private void printProductHeader() {

	    System.out.println(
	        "\n============================== AVAILABLE PRODUCTS =============================="
	    );

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

	    System.out.println(
	        "-------------------------------------------------------------------------------"
	    );
	}
	
	private boolean printProductList(List<Product> products) {

	    boolean hasSellableProducts = false;

	    for (Product product : products) {

	        if (!product.isSellable()) {
	            continue;
	        }

	        hasSellableProducts = true;

	        String discount =
	                product.getDiscountPercent() > 0
	                ? String.format("%.0f%%", product.getDiscountPercent())
	                : "-";


	        System.out.printf(
	                "%-10s %-20s %,12d %10s %,12d %8.1f %-8s%n",
	                product.getCode(),
	                product.getName(),
	                (long) product.getPrice(),
	                discount,
	                (long) product.getDiscountedPrice(),
	                product.getStock(),
	                product.getUnitType()
	        );
	    }

	    return hasSellableProducts;
	}
	
	private void printProductFooter(boolean hasSellableProducts) {

	    System.out.println(
	        "-------------------------------------------------------------------------------"
	    );

	    if (!hasSellableProducts) {
	        System.out.println("⚠️ No sellable products available.");
	    }
	}
		
	private void showCart(Cart cart) {

	    if (cart.getItems().isEmpty()) {
	        System.out.println("🛒 Your cart is empty.");
	        return;
	    }

	    printCartHeader();

	    printCartItems(cart);

	    printCartSummary(cart);

	    validator.pause();
	}

	private void printCartHeader() {

	    System.out.println("\n════════════════════════════════════════════════════════════════════════════");
	    System.out.println("                           🛒 YOUR SHOPPING CART");
	    System.out.println("════════════════════════════════════════════════════════════════════════════");

	    System.out.printf(
	            "%-22s %-6s %-8s %12s %8s %14s%n",
	            "Item",
	            "Qty",
	            "Unit",
	            "Price",
	            "Disc",
	            "Subtotal"
	    );

	    System.out.println(
	            "────────────────────── ────── ──────── ─────────── ─────── ─────────────"
	    );
	}
	
	private void printCartItems(Cart cart) {

	    for (CartItem ci : cart.getItems()) {

	        Product product = ci.getProduct();

	        String quantity =
	                (ci.getQuantity() == Math.floor(ci.getQuantity()))
	                ? String.valueOf((long) ci.getQuantity())
	                : String.format("%.1f", ci.getQuantity());


	        String discount =
	                product.getDiscountPercent() > 0
	                ? String.format("%.0f%%", product.getDiscountPercent())
	                : "-";


	        System.out.printf(
	                "%-22s %-6s %-8s %,12d %8s %,14d%n",
	                product.getName(),
	                quantity,
	                product.getUnitType(),
	                (long) product.getPrice(),
	                discount,
	                (long) ci.getTotalPrice()
	        );
	    }
	}
	
	
	private void printCartSummary(Cart cart) {

	    double originalTotal = 0;
	    double discountedTotal = 0;


	    for (CartItem item : cart.getItems()) {

	        Product product = item.getProduct();

	        originalTotal += 
	                product.getPrice() * item.getQuantity();

	        discountedTotal += 
	                item.getTotalPrice();
	    }


	    double saved = originalTotal - discountedTotal;


	    System.out.println(
	            "────────────────────────────────────────────────────────────────────────────"
	    );


	    System.out.printf(
	            " %-20s %,18d Tomans%n",
	            "Original Total:",
	            (long) originalTotal
	    );


	    System.out.printf(
	            " %-20s %,18d Tomans%n",
	            "Product Discount:",
	            (long) saved
	    );


	    System.out.printf(
	            " %-20s %,18d Tomans%n",
	            "Coupon Discount:",
	            0L
	    );


	    System.out.println(
	            "────────────────────────────────────────────────────────────────────────────"
	    );


	    System.out.printf(
	            " %-20s %,18d Tomans%n",
	            "Current Total:",
	            (long) discountedTotal
	    );


	    System.out.println(
	            "════════════════════════════════════════════════════════════════════════════"
	    );


	    if(saved > 0) {

	        System.out.printf(
	                "🎉 Great! You have already saved %,d Tomans on this purchase.%n",
	                (long) saved
	        );
	    }
	}
	
	
	private void checkout(Customer customer, Cart cart) {
	
	    if(cart.getItems().isEmpty()) {
	        System.out.println("Cart is empty.");
	        return;
	    }
	
	    printOrderSummary(cart);
	
	    double finalAmount = applyCoupon(cart);
	
	    PaymentMethod method =
	            getPaymentMethod(customer, finalAmount);
	
	    Invoice invoice =
	            createInvoice(cart, customer, method, finalAmount);
	
	    finishPurchase(customer, invoice);
	}

	private void printOrderSummary(Cart cart) {

	    System.out.println("\n--- Order Summary ---");

	    System.out.printf(
	            "Original amount: %,d Tomans%n",
	            (long) cart.getTotalAmount()
	    );

	    System.out.println("---------------------");
	}

	// For Coupon 
	private double applyCoupon(Cart cart) {
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

	private Invoice createInvoice(
	        Cart cart,
	        Customer customer,
	        PaymentMethod method,
	        double amount
	) {

	    Invoice invoice =
	            store.checkoutCart(
	                    cart,
	                    method,
	                    amount
	            );

	    if(appliedCoupon != null) {
	        appliedCoupon.incrementUsage();
	    }

	    return invoice;
	}
	
	private void finishPurchase(
	        Customer customer,
	        Invoice invoice
	) {

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

	    showPurchaseResult(invoice);
	    
	}
	
	private void showPurchaseResult(Invoice invoice){
	    System.out.println();
	    System.out.println(invoice);
	    System.out.println("✅ Purchase completed. Thank you!");
	}
	
	
	// edit customer information
	private void editCustomerInfo(Customer customer) {
	    navigation.push("Edit Info");

	    try {
	        navigation.printBreadcrumb();

	        System.out.println("\n--- Edit Info ---");
	        System.out.println("(Press Enter to keep the current value)");

	        boolean changed = updateCustomerName(customer)
	                | updateCustomerPhone(customer);

	        if (changed) {
	            store.save();

	            Logger.info(
	                    "Customer updated | Name: "
	                    + customer.getName()
	                    + " | Phone: "
	                    + customer.getPhone()
	            );

	            System.out.println("✅ Customer information updated successfully.");
	        } else {
	            System.out.println("ℹ️ No changes were made.");
	        }

	    } finally {
	        navigation.pop();
	    }
	}
	
	private boolean updateCustomerName(Customer customer) {

	    String newName = validator.readOptionalString(
	            "New name (current: " + customer.getName() + "): "
	    );

	    if (newName == null || newName.equals(customer.getName())) {
	        return false;
	    }

	    customer.setName(newName);
	    return true;
	}
	
	private boolean updateCustomerPhone(Customer customer) {

	    String newPhone = validator.readOptionalString(
	            "New phone (current: " + customer.getPhone() + "): "
	    );

	    if (newPhone == null || newPhone.isEmpty()
	            || newPhone.equals(customer.getPhone())) {
	        return false;
	    }


	    Customer existing = store.findCustomerByPhone(newPhone);

	    if (existing != null && existing != customer) {

	        Logger.warning(
	                "Customer update failed | Duplicate phone: "
	                + newPhone
	        );

	        System.out.println(
	                "❌ This phone number is already registered by another customer."
	        );

	        return false;
	    }

	    customer.setPhone(newPhone);
	    return true;
	}
	
	// show customer invoice
	private void showCustomerInvoices(Customer customer) {
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
	
	
	
	
	// return an item for loyal customer
	private void returnItem(LoyalCustomer customer) {
	    navigation.push("Return Item");
	    navigation.printBreadcrumb();

	    Invoice invoice = requestCustomerInvoice(customer);

	    if (invoice == null) {
	        navigation.pop();
	        return;
	    }

	    showInvoiceItems(invoice);

	    String productCode =
	            validator.readNonEmptyString("Product code to return: ");

	    System.out.print("Quantity to return: ");
	    double quantity = validator.readPositiveDouble();

	    processReturn(customer, invoice, productCode, quantity);

	    navigation.pop();
	}
	
	private Invoice requestCustomerInvoice(LoyalCustomer customer) {

	    System.out.println("\n--- Return an Item ---");

	    String invoiceId =
	            validator.readNonEmptyString("Enter invoice ID: ");

	    Invoice invoice = store.findInvoiceById(invoiceId);

	    if (invoice == null) {
	        System.out.println("❌ Invoice not found.");
	        return null;
	    }

	    if (!invoice.getCustomer().equals(customer)) {
	        System.out.println("❌ This invoice does not belong to you.");
	        return null;
	    }

	    return invoice;
	}
	
	private void showInvoiceItems(Invoice invoice) {

	    System.out.println("\nItems in this invoice:");

	    for (CartItem item : invoice.getItems()) {

	        System.out.println(
	                " - "
	                + item.getProduct().getCode()
	                + " ("
	                + item.getProduct().getName()
	                + ") x"
	                + item.getQuantity()
	        );
	    }
	}
	
	private void processReturn(
	        LoyalCustomer customer,
	        Invoice invoice,
	        String productCode,
	        double quantity
	) {

	    store.processReturn(
	            customer,
	            invoice,
	            productCode,
	            quantity
	    );

	    store.save();

	    Logger.info(
	            "Item returned | Customer: "
	            + customer.getName()
	            + " | Invoice: "
	            + invoice.getId()
	            + " | Product: "
	            + productCode
	            + " | Quantity: "
	            + quantity
	    );

	    System.out.println(
	            "✅ Returned "
	            + quantity
	            + " of "
	            + productCode
	            + ". Credit: "
	            + customer.getCredit()
	            + " Tomans."
	    );

	    validator.pause();
	}
	
	// show status of loyal custoemr
	private void viewFinancialStatus(LoyalCustomer lc) {
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
	private void payDebt(LoyalCustomer customer) {

	    navigation.push("Pay Debt");
	    navigation.printBreadcrumb();

	    Double amount = requestDebtPayment(customer);

	    if (amount == null) {
	        navigation.pop();
	        return;
	    }

	    processDebtPayment(customer, amount);

	    navigation.pop();
	}
	
	private Double requestDebtPayment(LoyalCustomer customer) {

	    System.out.println("\n--- Pay Debt ---");

	    System.out.println(
	            "Current debt: "
	            + String.format("%,d Tomans", (long) customer.getDebt())
	    );

	    if (customer.getDebt() == 0) {
	        System.out.println("✅ No debt to pay.");
	        return null;
	    }

	    if (!validator.yesOrNo("Do you want to pay off your debt?")) {
	        System.out.println("❌ Payment cancelled.");
	        return null;
	    }

	    Double amount = validator.readOptionalPositiveDouble(
	            "Amount to pay (or press Enter to skip): "
	    );

	    if (amount == null)
	        return null;

	    if (amount > customer.getDebt()) {
	        System.out.println("❌ Cannot pay more than your debt.");
	        return null;
	    }

	    return amount;
	}
	
	private void processDebtPayment(
	        LoyalCustomer customer,
	        double amount
	) {

	    customer.payDebt(amount);

	    store.save();

	    Logger.info(
	            "Debt payment | Customer: "
	                    + customer.getName()
	                    + " | Paid: "
	                    + amount
	                    + " | Remaining Debt: "
	                    + customer.getDebt()
	    );

	    System.out.println(
	            "✅ Paid "
	                    + String.format("%,.2f Tomans", amount)
	                    + ". Remaining debt: "
	                    + String.format("%,.2f Tomans", customer.getDebt())
	    );
	}
	
	// new member ship(for expiration code)
	private void renewMembershipCode(LoyalCustomer customer) {

	    navigation.push("Renew Membership");
	    navigation.printBreadcrumb();

	    if (!confirmMembershipRenewal(customer)) {
	        navigation.pop();
	        return;
	    }

	    generateNewMembershipCode(customer);

	    navigation.pop();
	}
	
	private boolean confirmMembershipRenewal(LoyalCustomer customer) {

	    System.out.println("\n--- Renew Membership Code ---");
	    System.out.println("Current code: " + customer.getMembershipCode());

	    if (!validator.yesOrNo("Generate a new membership code?")) {
	        System.out.println("❌ Renewal cancelled.");
	        return false;
	    }

	    return true;
	}
	
	private void generateNewMembershipCode(LoyalCustomer customer) {

	    String oldCode = customer.getMembershipCode();

	    String newCode =
	            store.generateMembershipCode(customer.getName());

	    customer.setMembershipCode(newCode);

	    store.save();

	    Logger.info(
	            "Membership code renewed | Customer: "
	                    + customer.getName()
	                    + " | Old: "
	                    + oldCode
	                    + " | New: "
	                    + newCode
	    );

	    System.out.println("✅ New membership code: " + newCode);
	    System.out.println("⚠️ Please save this code – you will need it to log in.");

	    validator.pause();
	}
	
	
}
