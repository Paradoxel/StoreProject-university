package com.storeapp.ui;

import java.util.List;

import com.storeapp.model.Invoice;
import com.storeapp.service.CryptoService;
import com.storeapp.service.Store;
import com.storeapp.util.Constants;
import com.storeapp.util.InputValidator;
import com.storeapp.util.Constants;
public class InvoiceManager {
	Store store;
	InputValidator validator;
	public InvoiceManager(Store store, InputValidator validator) {
        this.store = store;
        this.validator = validator;
    }
	
	
	

	public void showMenu() {
		String[] options = {
	            "1. View All Invoices",
	            "2. Find Invoice by ID",
	            "3. Find Invoices by Customer",
	            "4. Decrypt Secure Invoice Token",
	            "5. Back"
	    };
		
		while(true) {
			validator.printBox("INVOICE MANAGEMENT", options);
			
			int choice = validator.readIntRange(1, 5);
			switch (choice) {
				case 1:
					showAllInvoices();
					break;
				case 2:
					findInvoiceById();
					break;
				case 3:
					//findInvoicesByCustomer();
					break;
				case 4:
					decryptInvoiceToken();
					break;
				case 5:
					return;
					
			}
		}
		
		
		
	}
	
	private void decryptInvoiceToken() {
		try {
			String token = validator.readNonEmptyString(
		            "Enter Secure Invoice Token: ");

		    String decrypted = CryptoService.decrypt(token);

		    String[] parts = decrypted.split(Constants.SEPARATOR);

		    System.out.println("\n════════ Secure Invoice Details ════════");
		    System.out.println("Phone Number : " + parts[0]);
		    System.out.println("Date         : " + parts[1]);
		    System.out.println("Final Amount : " + parts[2] + " Tomans");
		    System.out.println("═════════════════════════════════════════");
		    validator.pause();
		}
		catch (RuntimeException e) {
		    System.out.println("❌ Invalid secure invoice token.");
		}
	    
	}
	

	
	private void printInvoiceTable(List<Invoice> invoices) {
		if (invoices.isEmpty()) {
			System.out.println("\n⚠️ No invoices to display.");
			return;
		}

		System.out.println("\n--- Invoice History ---");
		System.out.printf("%-20s %-15s %-20s %-10s %-10s%n",
				"Invoice #", "Customer", "Date", "Amount", "Payment");
		System.out.println("-------------------- --------------- -------------------- ---------- ----------");



		for (Invoice inv : invoices) {
			System.out.printf("%-20s %-15s %-20s %,10d %-10s%n",
					shortId(inv.getId()),
					inv.getCustomer().getName(),
					inv.getDateTime().format(Constants.DISPLAY_DATETIME_FORMAT),
					(long) inv.getFinalAmount(),
					inv.getPaymentMethod());
		}

		System.out.println("-------------------- --------------- -------------------- ---------- ----------");
	}
	
	public void showAllInvoices() {
		printInvoiceTable(store.getInvoices());
		validator.pause();
	}
	
	private String shortId(String id) {
		int visibleChars = 17;
		if (id.length() > visibleChars) {
			return id.substring(0,visibleChars)+"...";
		}
		return id;
	}
	
	
	private void findInvoiceById() {
		String id = validator.readNonEmptyString("Enter Invoice ID: ");
		Invoice invoice = store.findInvoiceById(id);
		if (invoice == null) {
			System.out.println("\n⚠️ No invoice found with that ID.");
			validator.pause();
			return;
		}
		System.out.println(invoice);
		validator.pause();	
	}
	
	
}
