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

        String status;

        if (!active) {
            status = "Inactive";
        } else if (isExpired()) {
            status = "Expired";
        } else if (isUsageLimitReached()) {
            status = "Limit Reached";
        } else {
            status = "Available";
        }

        return String.format(
                "[%s]  %,.0f%% OFF  |  Status: %-13s  |  Expires: %s  |  Usage: %d/%d",
                code,
                discountPercentage,
                status,
                expirationDate,
                usedCount,
                maxUsage
        );
    }
    
    
    
    // Setters
    public void setDiscountPercentage(double newDiscountPercentage) {
    	this.discountPercentage=newDiscountPercentage;
    }
    
    public void setExpirationDate(LocalDate newExpirationDate) {
    	this.expirationDate=newExpirationDate;
    }
    
    public void setMaxUsage(int newMaxUsage) {
    	this.maxUsage=newMaxUsage;
    }
    
    
    
}
