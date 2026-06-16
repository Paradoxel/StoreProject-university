package com.storeapp.service;

import java.time.LocalDate;
import java.util.Random;

import com.storeapp.model.Product;
import com.storeapp.model.*;
import com.storeapp.util.Constants;

public class RandomDataGenerator {
	private Store store;
	private Random random;
	
	public RandomDataGenerator(Store store) {
		this.store=store;
		this.random=new Random();
	}
	//  Generates a product with random mandatory fields and optional details.
	private Product generateRandomProduct() {
		// create a unique code for product 
		String code=store.generateProductCode();
		// fake name for product
		String productName=Constants.SAMPLE_NAMES[random.nextInt(Constants.SAMPLE_NAMES.length)];
		// fake price for product
		double price=1000000+random.nextInt(5000000);
		// fake stock
		double stock=random.nextInt(50)+1;
		// fake unit type 
		UnitType[] units = UnitType.values();
		UnitType unit=units[random.nextInt(units.length)];
		
		Product.Builder builder=new Product.Builder(code, productName, price, stock, unit);
		
		// fake manufacturer
		if(random.nextBoolean()) {
			builder.manufacturer(Constants.MANUFACTURERS[random.nextInt(Constants.MANUFACTURERS.length)]);
		}
		// fake color
		if(random.nextBoolean()) {
			builder.color(Constants.COLORS[random.nextInt(Constants.COLORS.length)]);
		}
		// fake weight in kg
		if(random.nextBoolean()) {
			builder.weight((random.nextDouble()*10)+1);
		}
		// fake vol
		if(random.nextBoolean()) {
			builder.volume((random.nextDouble()*10)+1);
		}
		// fake nextBoolean
		if(random.nextBoolean()) {
			builder.description("Random product description for product with code: "+ code);
		}
		// fake discount
		if(random.nextBoolean()) {
			builder.discountPercent(random.nextInt(50)+1);
		}
		// fake production date 
		if(random.nextBoolean()) {
			builder.productionDate(LocalDate.now().minusDays(random.nextInt(365)));
		}
		// fake expiration date
		if(random.nextBoolean()) {
			builder.expirationDate(LocalDate.now().plusDays(random.nextLong(365)+10));
		}
		
		// generate the product object
		return builder.build();
	}
	
	
	public void generateProducts(int count) {
		Logger.log("Random product generation started – Count: " + count);
	    for (int i = 0; i < count; i++) {
	        store.addProduct(generateRandomProduct());
	        System.out.println("Product " + (i+1) + " generated successfully.");
	    }
	    System.out.println("Successfully generated all " + count + " products.");
	    Logger.log("Random product generation completed – Total: " + count);
	}
	
	
	
	// Generate a random customer (regular  or Loyal)
	private Customer generateRandomCustomer() {
		// random name from existing list (reuse manufacturer names)
		String name=Constants.MANUFACTURERS[random.nextInt(Constants.MANUFACTURERS.length)];
		// random phone number
		String phone="09"+(100000000 + random.nextInt(900000000));
		// randomly decide if Loyal (50% chance)
		if(random.nextBoolean()) {
			String memberCode=store.generateMembershipCode(name);
			return new LoyalCustomer(name,phone,memberCode,LocalDate.now());
		}
		return new Customer(name, phone);
	}
	
	public void generateCustomers(int count) {
		Logger.log("Random customer generation started – Count: " + count);
		for(int i=0;i<count;i++) {
			store.addCustomer(generateRandomCustomer());
			System.out.println("Customer " + (i + 1) + " generated successfully.");
		}
		System.out.println("Successfully generated " + count + " random customers.");
		Logger.log("Random customer generation completed – Total: " + count);
	}
	
	
}
