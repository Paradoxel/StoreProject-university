package com.storeapp.ui;

import java.util.ArrayList;
import java.util.List;

import com.storeapp.model.Customer;
import com.storeapp.model.Invoice;
import com.storeapp.model.LoyalCustomer;
import com.storeapp.service.CryptoService;
import com.storeapp.service.Logger;
import com.storeapp.service.Store;
import com.storeapp.util.Constants;
import com.storeapp.util.InputValidator;
public class InvoiceManager {
	private Store store;
	private InputValidator validator;
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
					findInvoicesByCustomer();
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
		    if (parts.length != 3) {
		    	
		        throw new RuntimeException("Invalid token");
		    }
		    System.out.println("\n════════ Secure Invoice Details ════════");
		    System.out.println("Phone Number : " + parts[0]);
		    System.out.println("Date         : " + parts[1]);
		    System.out.println("Final Amount : " + parts[2] + " Tomans");
		    System.out.println("═════════════════════════════════════════");
		    validator.pause();
		}
		catch (RuntimeException e) {
			Logger.warning(
				    "Invalid invoice token entered."
				);
		    System.out.println("❌ Invalid secure invoice token.");
		}
	    
	}
	

	
	private void printInvoiceTable(List<Invoice> invoices) {
		if (invoices.isEmpty()) {
			System.out.println("\n⚠️ No invoices to display.");
			return;
		}

		int idW = 20, nameW = 15, phoneW = 13, dateW = 20, itemsW = 6, amountW = 16, payW = 10;

		String top    = "┌" + "─".repeat(idW+2) + "┬" + "─".repeat(nameW+2) + "┬" + "─".repeat(phoneW+2) + "┬" + "─".repeat(dateW+2) + "┬" + "─".repeat(itemsW+2) + "┬" + "─".repeat(amountW+2) + "┬" + "─".repeat(payW+2) + "┐";
		String mid    = "├" + "─".repeat(idW+2) + "┼" + "─".repeat(nameW+2) + "┼" + "─".repeat(phoneW+2) + "┼" + "─".repeat(dateW+2) + "┼" + "─".repeat(itemsW+2) + "┼" + "─".repeat(amountW+2) + "┼" + "─".repeat(payW+2) + "┤";
		String bottom = "└" + "─".repeat(idW+2) + "┴" + "─".repeat(nameW+2) + "┴" + "─".repeat(phoneW+2) + "┴" + "─".repeat(dateW+2) + "┴" + "─".repeat(itemsW+2) + "┴" + "─".repeat(amountW+2) + "┴" + "─".repeat(payW+2) + "┘";

		System.out.println("\n--- Invoice History ---");
		System.out.println(top);
		System.out.printf("│ %-" + idW + "s │ %-" + nameW + "s │ %-" + phoneW + "s │ %-" + dateW + "s │ %" + itemsW + "s │ %" + amountW + "s │ %-" + payW + "s │%n",
				"Invoice #", "Customer", "Phone", "Date", "Items", "Amount (Tomans)", "Payment");
		System.out.println(mid);

		double totalAmount = 0;
		int cashCount = 0, creditCount = 0;

		for (Invoice inv : invoices) {
			System.out.printf("│ %-" + idW + "s │ %-" + nameW + "s │ %-" + phoneW + "s │ %-" + dateW + "s │ %" + itemsW + "d │ %," + amountW + "d │ %-" + payW + "s │%n",
					shortId(inv.getId()),
					inv.getCustomer().getName(),
					inv.getCustomer().getPhone(),
					inv.getDateTime().format(Constants.DISPLAY_DATETIME_FORMAT),
					inv.getItems().size(),
					(long) inv.getFinalAmount(),
					inv.getPaymentMethod());

			totalAmount += inv.getFinalAmount();
			if (inv.getPaymentMethod().toString().equals("CASH")) {
				cashCount++;
			} else {
				creditCount++;
			}
		}

		System.out.println(bottom);
		System.out.printf("%nShowing %d invoice(s)  |  Total: %,d Tomans  |  Cash: %d  |  Credit: %d%n",
				invoices.size(), (long) totalAmount, cashCount, creditCount);
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
			Logger.warning(
				    "Invoice not found: " + id
				);
			System.out.println("\n⚠️ No invoice found with that ID.");
			validator.pause();
			return;
		}
		Logger.info(
			    "Invoice viewed: " + id
			);
		System.out.println(invoice);
		validator.pause();	
	}
	
	
	
	
	private void findInvoicesByCustomer() {
		String[] options = {
				"1. By Phone Number",
				"2. By Name",
				"3. By Membership Code",
				"4. Back"
		};

		while(true) {
			validator.printBox("FIND INVOICES BY CUSTOMER", options);
			int choice = validator.readIntRange(1, 4);
			switch(choice) {
			case 1:
				searchByPhone();
				break;
			case 2:
				searchByName();
				break;
			case 3:
				searchByMembershipCode();
				break;
			case 4:
				return;
			}
		}
		
		
		
		
	}
	
	private void searchByPhone() {
		String phone = validator.readPhoneNumber();

		List<Invoice> results = new ArrayList<>();
		for (Invoice inv : store.getInvoices()) {
			if (inv.getCustomer().getPhone().equals(phone)) {
				results.add(inv);
			}
		}



		printInvoiceTable(results);
		validator.pause();
	}
	
	
	private void searchByName() {
		String name = validator.readNonEmptyString(
				"Enter customer name (or part of it): ").toLowerCase();

		List<Invoice> results = new ArrayList<>();
		for (Invoice inv : store.getInvoices()) {
			if (inv.getCustomer().getName().toLowerCase().contains(name)) {
				results.add(inv);
			}
		}


		printInvoiceTable(results);
		validator.pause();
	}
	
	
	private void searchByMembershipCode() {
		String code = validator.readNonEmptyString("Enter Membership Code: ");

		List<Invoice> results = new ArrayList<>();
		for (Invoice inv : store.getInvoices()) {
			Customer c = inv.getCustomer();
			if (c instanceof LoyalCustomer lc && lc.getMembershipCode().equals(code)) {
				results.add(inv);
			}
		}

		printInvoiceTable(results);
		validator.pause();
	}
	
	
}
