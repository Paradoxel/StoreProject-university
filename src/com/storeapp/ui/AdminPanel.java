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
		while(true) {
			
			System.out.println("\n--- Admin Panel ---");
			System.out.println("1. Show all products");
			System.out.println("2. Add product");
			System.out.println("3. Edit product");      
			System.out.println("4. Delete product");
			System.out.println("5. Search product");
			System.out.println("6. Add loyal customer");
			System.out.println("7. Back to main menu");
			
			int choice = validator.readIntRange(1, 7);
			switch (choice) {
		    case 1: showAllProducts(); break;
		    case 2: addProduct(); break;
		    case 3: editProduct(); break;      
		    case 4: deleteProduct(); break;
		    case 5: searchProduct(); break;
		    case 6: addLoyalCustomer(); break;
		    case 7: return;
		    default: System.out.println("Invalid option.");
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
    	System.out.println("\n--- Add New Product ---");
    	
    	// Mandatory fields
    	String code;
    	while(true) {
    		code=validator.readNonEmptyString("Code: ");
    		if(store.findItemByCode(code)==null)
    			break;
    		System.out.println("❌ This code already exists. Please enter a different code.");
    	}
    	
    	
    	String name=validator.readNonEmptyString("Name: ");
    	System.out.print("Price: ");
    	double price=validator.readPositiveDouble();
    	System.out.print("Stock: ");
    	double stock=validator.readPositiveDouble();
    	UnitType unitType=validator.readUnitType();
    	
    	Product.Builder builder = new Product.Builder(code, name, price, stock, unitType);
 
    	if(validator.yesOrNo("Do you want to add optional details?")){
    		
    		System.out.println("\n--- Optional Fields ---");
    		System.out.println("Preese enter to skip");
    		String man=validator.readOptionalString("Manufacturer: ");
    		if (man!=null) builder.manufacturer(man);
    		
    		String col = validator.readOptionalString("Color: ");
            if (col != null) builder.color(col);

            System.out.print("Weight (kg, 0 to skip): ");
            double weight = validator.readPositiveDouble();
            if (weight > 0) builder.weight(weight);

            System.out.print("Volume (L, 0 to skip): ");
            double volume = validator.readPositiveDouble();
            if (volume > 0) builder.volume(volume);

            String desc = validator.readOptionalString("Description: ");
            if (desc != null) builder.description(desc);

            System.out.print("Discount Percent (0-100): ");
            double discount = validator.readDiscountPercent();
            builder.discountPercent(discount);
    	}
    	
    	Product product=builder.build();
    	store.addProduct(product);
    	try {
			store.saveToFile(Constants.STORE_FILE);
			System.out.println("✅ Product '" + product.getName() + "' added successfully!");
		} catch (IOException e) {
			System.out.print("Error...\nTry again" );
		}
    	
    }

    private void searchProduct() {
    	System.out.println("\n--- Search Product ---");
    	String keyword = validator.readNonEmptyString("Enter keyword to search (name or code): ");
    	List<Product> results = store.searchItems(keyword);
    	if (results.isEmpty()) {
            System.out.println("No products found matching '" + keyword + "'.");
            return;
        }
    	System.out.println("\n--- Search Results for '" + keyword + "' ---");
        for (Product p : results) {
            System.out.println(p.toString());
        }
    }


    private void addLoyalCustomer() {
    	System.out.println("\n--- Add Loyal Customer ---");
    	String name=validator.readNonEmptyString("Name: ");
    	String phone;
    	while (true) {
    	    phone = validator.readNonEmptyString("Phone: ");
    	    if (store.findCustomerByPhone(phone) == null) {
    	        break; 
    	    }
    	    System.out.println("❌ This phone number is already registered. Please enter a different one.");
    	}
    	String membershipCode = store.generateMembershipCode(name);
    	System.out.println("Membership Code (auto-generated): " + membershipCode);
    	System.out.println("⚠️ Please save this code securely – the customer will need it to log in.");
    	LocalDate joinDate=LocalDate.now();
    	System.out.println("Join Date (today): " + joinDate);
    	LoyalCustomer loyalCustomer = new LoyalCustomer(name, phone, membershipCode, joinDate);
    	store.addCustomer(loyalCustomer);
    	try {
            store.saveToFile(Constants.STORE_FILE);
            System.out.println("✅ Loyal customer '" + loyalCustomer.getName() + "' added successfully!");
        } catch (IOException e) {
            System.out.println("❌ Error saving customer. Please try again.");
        }
    	
    }
    
    
    private void deleteProduct() {
        System.out.println("\n--- Delete Product ---");
        String code = validator.readNonEmptyString("Enter the product code to delete: ");
        
        Product product = store.findItemByCode(code);
        if (product == null) {
            System.out.println("❌ No product found with code '" + code + "'.");
            return;
        }
        
        // Remove from the list
        store.removeProduct(product);
        
        // Save changes to file
        try {
            store.saveToFile(Constants.STORE_FILE);
            System.out.println("✅ Product '" + product.getName() + "' deleted successfully!");
        } catch (IOException e) {
            System.out.println("❌ Product deleted in memory, but could not save to file.");
        }
    }
    
    private void editProduct() {
        System.out.println("\n--- Edit Product ---");
        String code = validator.readNonEmptyString("Enter the product code to edit: ");
        
        Product product = store.findItemByCode(code);
        if (product == null) {
            System.out.println("❌ No product found with code '" + code + "'.");
            return;
        }

        // Show current values
        System.out.println("\nEditing product: " + product.getName());
        System.out.println("(Press Enter to keep the current value)\n");

        // Price
        System.out.print("New price (current: " + product.getPrice() + "): ");
        Double newPrice = validator.readOptionalDouble();
        if (newPrice != null && newPrice > 0) {
            product.setPrice(newPrice);
        }

        // Stock
        System.out.print("New stock (current: " + product.getStock() + "): ");
        Double newStock = validator.readOptionalDouble();
        if (newStock != null && newStock >= 0) {
            product.setStock(newStock);
        }

        // Discount
        System.out.print("New discount (current: " + product.getDiscountPercent() + "%): ");
        Double newDiscount = validator.readOptionalDouble();
        if (newDiscount != null && newDiscount >= 0 && newDiscount <= 100) {
            product.setDiscountPercent(newDiscount);
        }

        // Save changes
        try {
            store.saveToFile(Constants.STORE_FILE);
            System.out.println("✅ Product '" + product.getName() + "' updated successfully!");
        } catch (IOException e) {
            System.out.println("❌ Product updated in memory, but could not save to file.");
        }
    }
	
	
	
	
	
	
	
	
}
