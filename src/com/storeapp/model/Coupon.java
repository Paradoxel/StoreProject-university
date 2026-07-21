package com.storeapp.model;

import java.time.LocalDate;

public class Coupon {
	private String code;
    private int discountPercentage;
    private boolean active;
    private LocalDate expirationDate;
    private int maxUsage;
    private int usedCount;

    public Coupon(String code,
                  int discountPercentage,
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
    
    public int getDiscountPercentage() {
    	return discountPercentage;
    }
    
    public LocalDate getExpirationDate() {
    	return expirationDate;
    }
    
    
    
}
