package com.storeapp.ui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import com.storeapp.model.*;
import com.storeapp.service.*;
import com.storeapp.util.Constants;
import com.storeapp.util.InputValidator;

public class AdminPanel {
	private InputValidator validator;
	private Store store;
	public AdminPanel(Store store, InputValidator validator) {
	    this.store = store;
	    this.validator = validator;
	}
	
	public void showMenu() {
	    ProductManager productManager = new ProductManager(store, validator);
	    CustomerManager customerManager = new CustomerManager(store, validator);

	    String[] options = {
	        "1. Product Management",
	        "2. Customer Management",
	        "3. Back to main menu"
	    };

	    while (true) {
	        validator.printBox("ADMIN PANEL", options);

	        int choice = validator.readIntRange(1, 3);

	        switch (choice) {
	            case 1:
	                productManager.showMenu();
	                break;
	            case 2:
	                customerManager.showMenu();
	                break;
	            case 3:
	                return;
	        }
	    }
	}
	
}
