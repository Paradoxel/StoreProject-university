package com.storeapp.ui;

import com.storeapp.service.Store;
import com.storeapp.util.InputValidator;
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
	


}
