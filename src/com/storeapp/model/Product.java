package com.storeapp.model;

import java.io.Serializable;
import java.time.LocalDate;

// Represents a product in the store
public class Product implements Serializable {
	
	// Mandatory fields
	private String code;
	private String name;
	private double price;
	private double stock;
	private UnitType unitType; // dependency
	
	// optional fields
	private String manufacturer;
	private String color;
	private Double weight; // null able
	private Double volume; // null able
	private String description;
	private double discountPercent;// o to 100
	private LocalDate productionDate;
	private LocalDate expirationDate;
	
	private Product(Builder builder) {
		this.code = builder.code;
        this.name = builder.name;
        this.price = builder.price;
        this.stock = builder.stock;
        this.unitType = builder.unitType;
        this.manufacturer = builder.manufacturer;
        this.color = builder.color;
        this.weight = builder.weight;
        this.volume = builder.volume;
        this.description = builder.description;
        this.discountPercent = builder.discountPercent;
        this.productionDate = builder.productionDate;
        this.expirationDate = builder.expirationDate;

	}
	
	public static class Builder{
		// Mandatory fields
		private final String code;
        private final String name;
        private final double price;
        private final double stock;
        private final UnitType unitType;
        // optional fields
        private String manufacturer;
        private String color;
        private Double weight;
        private Double volume;
        private String description;
        private double discountPercent = 0.0;
        private LocalDate productionDate;
        private LocalDate expirationDate;
        
        public Builder(String code, String name, double price, double stock, UnitType unitType) {
            this.code = code;
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.unitType = unitType;
        }
        // Method Chaining
        public Builder manufacturer(String val) { manufacturer = val; return this; }
        public Builder color(String val) { color = val; return this; }
        public Builder weight(Double val) { weight = val; return this; }
        public Builder volume(Double val) { volume = val; return this; }
        public Builder description(String val) { description = val; return this; }
        public Builder discountPercent(double val) { discountPercent = val; return this; }
        public Builder productionDate(LocalDate val) { productionDate = val; return this; }
        public Builder expirationDate(LocalDate val) { expirationDate = val; return this; }
        // signal to main constructor
        public Product build() {
            return new Product(this);
        }

	}
	
	// Getters (the fields is private)

    public String getCode() { return code; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getStock() { return stock; }
    public UnitType getUnitType() { return unitType; }
    public String getManufacturer() { return manufacturer; }
    public String getColor() { return color; }
    public Double getWeight() { return weight; }
    public Double getVolume() { return volume; }
    public String getDescription() { return description; }
    public double getDiscountPercent() { return discountPercent; }
    public LocalDate getProductionDate() { return productionDate; }
    public LocalDate getExpirationDate() { return expirationDate; }
	
	
	// Setters(only fields that may need updating and changing
    public void setPrice(double price) {this.price=price;}
    public void setStock(double stock) {this.stock=stock;}
    public void setDiscountPercent(double discountPercent ) {this.discountPercent=discountPercent;}
    
	// price after applying the Discount
    public double getDiscountedPrice() {
    	return price*(1-(discountPercent/100));
    }
    
    // Total value 
    public double getTotalValue() {
    	return price*stock;
    }
    
    // Reduce Stock by the given amount
    public boolean reduceStock(double amount) {
    	if (amount>0 && amount<=stock) {
    		stock-=amount;
    		return true;
    	}
    	return false;
    }
    
    // Increase Stock
    public void increaseStock(double amount) {
    	if(amount>0)
    		stock+=amount;
    }
    
    // check if enough exists
    public boolean hasEnoughStock(double amount) {
    	return amount>0 && amount<=stock;
    }
    
    // check if the product has expired
    public boolean isExpired() {
    	if (expirationDate!=null) {
    		return LocalDate.now().isAfter(expirationDate);
    	}
    	return false;
    }
    
    // A product is sell able or not
    public boolean isSellable() {
    	return stock>0 && !isExpired();
    }
    
    
    // for showing the object 
    @Override
    public String toString() {
        return String.format("%s (%s) - Price: %,.0f Tomans - Stock: %.2f %s",
                name, code, price, stock, unitType);
    }
    
	
	
	
}
