package com.storeapp.ui;

import java.time.LocalDate;
import java.util.List;

import com.storeapp.model.Coupon;
import com.storeapp.service.Store;
import com.storeapp.util.Constants;
import com.storeapp.util.InputValidator;

public class CouponManager  {
	private Store store;
	private InputValidator validator;
	public CouponManager(Store store, InputValidator validator) {
		this.store=store;
		this.validator=validator;
	}
	
	
	public void showMenu() {
	    String[] options = {
	            "1. Add Coupon",
	            "2. View Coupons",
	            "3. Back"
	    };

	    while (true) {
	        validator.printBox("COUPON MANAGEMENT", options);

	        int choice = validator.readIntRange(1, 3);

	        switch (choice) {
	            case 1:
	                addCoupon();
	                break;

	            case 2:
	                viewCoupons();
	                break;

	            case 3:
	                return;
	        }
	    }
	}
	
	
	
	private void addCoupon() {

	    validator.printTitle("ADD COUPON");

	    String code = readUniqueCouponCode();

	    double discount = validator.readRequiredDiscountPercentage(
	            "Discount Percentage: ");

	    LocalDate expirationDate = validator.readDate(
	            "Expiration Date (yyyy-MM-dd): ");

	    System.out.print("Maximum Usage: ");
	    int maxUsage = validator.readIntRange(1, Constants.MAX_COUPON_USAGE);

	    Coupon coupon = new Coupon(
	            code,
	            discount,
	            expirationDate,
	            maxUsage
	    );

	    store.addCoupon(coupon);

	    System.out.println("\n✅ Coupon '" + code + "' created successfully.");

	    validator.pause();
	}

	private void viewCoupons() {

	    validator.printTitle("COUPON LIST");
	    List<Coupon> coupons = store.getCoupons();
	    if (coupons.isEmpty()) {
	        System.out.println("⚠️ No coupons available.");
	        validator.pause();
	        return;
	    }

	    int index = 1;
	    for (Coupon coupon : coupons) {
	        System.out.println(index++ + ". " + coupon);
	    }

	    validator.pause();
	}
	
	
	
	
	private String readUniqueCouponCode() {
	    while (true) {
	        String code = validator.readNonEmptyString("Coupon Code: ").trim().toUpperCase();

	        if (store.findCouponByCode(code) == null) {
	            return code;
	        }

	        System.out.println("❌ Coupon code already exists. Please enter another code.");
	    }
	}
	
	
	
	
	
}
