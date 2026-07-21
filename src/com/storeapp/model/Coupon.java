package com.storeapp.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Coupon implements Serializable {
	private String code;
    private double discountPercentage;
    private boolean active;
    private LocalDate expirationDate;
    private int maxUsage;
    private int usedCount;

    public Coupon(String code,
            double discountPercentage,
            LocalDate expirationDate,
            int maxUsage) {
	
	  this.code = code;
	  this.discountPercentage = discountPercentage;
	  this.expirationDate = expirationDate;
	  this.maxUsage = maxUsage;
	
	  this.active = true;
	  this.usedCount = 0;
	}
    
    
    
    public boolean isExpired() {
    	return LocalDate.now().isAfter(expirationDate);
    }
    
    public boolean isUsageLimitReached() {
    	return usedCount>=maxUsage;
    }
    
    public boolean isAvailable() {
    	return (active && !isExpired() && !isUsageLimitReached());
    }
    
    
    public void incrementUsage() {
    	if(!isUsageLimitReached()) {
    		usedCount++;
    	}
    	
    }
    
    public void deactivate() {
    	active=false;
    }
    
    // Getters
    public String getCode() {
    	return code;
    }
    
    public double getDiscountPercentage() {
    	return discountPercentage;
    }
    
    public LocalDate getExpirationDate() {
    	return expirationDate;
    }
    
    public boolean isActive() {
        return active;
    }

    public int getMaxUsage() {
        return maxUsage;
    }

    public int getUsedCount() {
        return usedCount;
    }
    
    // Calculator total amount
    
    public double calculateDiscount(double amount) {
        return amount * (discountPercentage / 100.0);
    }
    
    public double applyDiscount(double amount) {
        return amount - calculateDiscount(amount);
    }
    
    
    @Override
    public String toString() {
        return String.format(
            "Code: %-12s | Discount: %5.1f%% | Status: %-11s | Expiration: %s | Usage: %d/%d",
            code,
            discountPercentage,
            isAvailable() ? "Available" : "Unavailable",
            expirationDate,
            usedCount,
            maxUsage
        );
    }
    
    
    
}
