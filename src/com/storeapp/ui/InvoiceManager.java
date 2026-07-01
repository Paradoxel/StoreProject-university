package com.storeapp.ui;

import com.storeapp.service.Store;
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
	            "1. Decrypt Secure Invoice Token",
	            "2. Back"
	    };
		
		while(true) {
			validator.printBox("INVOICE MANAGEMENT", options);
			
			int choice = validator.readIntRange(1, 2);
			switch (choice) {
				case 1:
					decryptInvoiceToken();
					break;
				case 2:
					return;
					
			}
		}
		
		

		
	}
	
	private void decryptInvoiceToken() {
		
	}
	
	
}
