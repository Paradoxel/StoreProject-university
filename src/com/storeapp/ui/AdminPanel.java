package com.storeapp.ui;

import java.util.List;
import java.util.Scanner;

import com.storeapp.model.*;
import com.storeapp.service.*;
import com.storeapp.util.InputValidator;

public class AdminPanel {
	private InputValidator validator;
	private Store store;
	public AdminPanel(Store store, InputValidator validator) {
	    this.store = store;
	    this.validator = validator;
	}
	
	public void showMenu() {
		while(true) {
			
			System.out.println("\n--- Admin Panel ---");
			System.out.println("1. Show all products");
			System.out.println("2. Add product");
			System.out.println("3. Search product");
			System.out.println("4. Add loyal customer");
			System.out.println("5. Back to main menu");
			
			int choice = validator.readIntRange(1, 5);
			switch (choice) {
	        case 1:
	            showAllProducts();
	            break;
	        case 2:
	            addProduct();
	            break;
	        case 3:
	            searchProduct();
	            break;
	        case 4:
	            addLoyalCustomer();
	            break;
	        case 5:
	            return; 
	        default:
	            System.out.println("Invalid option. Try again.");
			}
		}
		
	}
	
	
	
	private void showAllProducts() {
        List<Product> products = store.getProducts();
        if (products.isEmpty()) {
            System.out.println("\nNo products in the store.");
            return;
        }
        System.out.println("\n--- Product List ---");
        for (Product p : products) {
            System.out.println(p.toString());
        }
    }


    private void addProduct() {
        System.out.println("Add product - coming soon...");
    }

    private void searchProduct() {
        System.out.println("Search product - coming soon...");
    }


    private void addLoyalCustomer() {
        System.out.println("Add loyal customer - coming soon...");
    }
	
	
	
	
	
	
	
	
}
