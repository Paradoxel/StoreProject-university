package com.storeapp.ui;

import com.storeapp.service.CryptoService;
import com.storeapp.service.Store;
import com.storeapp.util.Constants;
import com.storeapp.util.InputValidator;

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
					//showAllInvoices();
					break;
				case 2:
					//findInvoiceById();
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
	
	
}
