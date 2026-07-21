package com.storeapp.ui;

import com.storeapp.service.Store;
import com.storeapp.util.InputValidator;

public class CouponManager {
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
	    // TODO: Implement adding a new coupon
	}

	private void viewCoupons() {
	    // TODO: Implement viewing all coupons
	}
	
	
}
