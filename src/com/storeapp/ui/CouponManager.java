package com.storeapp.ui;

import java.time.LocalDate;
import java.util.List;

import com.storeapp.model.Coupon;
import com.storeapp.service.Logger;
import com.storeapp.service.Store;
import com.storeapp.ui.navigation.Navigation;
import com.storeapp.util.Constants;
import com.storeapp.util.InputValidator;

public class CouponManager  {
	private Store store;
	private InputValidator validator;
	private Navigation navigation;
	public CouponManager(
		    Store store,
		    InputValidator validator,
		    Navigation navigation
		) {
		    this.store = store;
		    this.validator = validator;
		    this.navigation = navigation;
		}
	
	
	public void showMenu() {
		navigation.push("Coupons");
	    String[] options = {
	            "1. Add Coupon",
	            "2. View Coupons",
	            "3. Edit Coupon",
	            "4. Change Coupon Status",
	            "5. Delete Coupon",
	            "6. Back"
	    };

	    while (true) {
	    	navigation.printBreadcrumb();
	        validator.printBox("COUPON MANAGEMENT", options);

	        int choice = validator.readIntRange(1, 6);

	        switch (choice) {

	            case 1:
	                addCoupon();
	                break;

	            case 2:
	                viewCoupons();
	                break;

	            case 3:
	                editCoupon();
	                break;

	            case 4:
	                changeCouponStatus();
	                break;

	            case 5:
	                deleteCoupon();
	                break;

	            case 6:
	            	navigation.pop();
	                return;
	        }
	    }
	}
	
	
	
	private void addCoupon() {
		navigation.push("Add Coupon");
		navigation.printBreadcrumb();
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
	    store.save();

	    Logger.info(
	        "Coupon added: "
	        + coupon.getCode()
	        + " | Discount: "
	        + coupon.getDiscountPercentage()
	        + "% | Expiration: "
	        + coupon.getExpirationDate()
	        + " | Max Usage: "
	        + coupon.getMaxUsage()
	    );

	    System.out.println("\n✅ Coupon '" + code + "' created successfully.");

	    validator.pause();
	    navigation.pop();

	}

	private void viewCoupons() {
		navigation.push("View Coupons");
		navigation.printBreadcrumb();
	    validator.printTitle("COUPON LIST");
	    List<Coupon> coupons = store.getCoupons();
	    if (coupons.isEmpty()) {
	        System.out.println("⚠️ No coupons available.");
	        validator.pause();
	        navigation.pop();
	        return;
	    }

	    int index = 1;
	    for (Coupon coupon : coupons) {
	        System.out.println(index++ + ". " + coupon);
	    }

	    validator.pause();
	    navigation.pop();
	}
	
	private void editCoupon() {
		navigation.push("Edit Coupon");
		navigation.printBreadcrumb();
		validator.printTitle("EDIT COUPON");
		Coupon coupon = findExistingCoupon();

		if (coupon == null) {
		    System.out.println("❌ Coupon not found.");
		    validator.pause();
		    navigation.pop();
		    return;
		}
		boolean updated = false;
		System.out.println("(Press Enter to keep the current value)");

		Double newDiscount = validator.readOptionalDiscountPercentage(
		        "New discount percentage (current: "
		        + coupon.getDiscountPercentage() + "%): "
		);

		if (newDiscount != null) {
			updated = true;
		    coupon.setDiscountPercentage(newDiscount);
		}
		

		LocalDate newExpiration = validator.readOptionalDate(
			    "New expiration date (current: " + coupon.getExpirationDate() + "): "
			);

		if (newExpiration != null) {
			updated = true;
		    coupon.setExpirationDate(newExpiration);
		}
		

		
		
		
		Integer newMaxUsage = validator.readOptionalIntRange(
		        "New maximum usage (current: " + coupon.getMaxUsage() + "): ",
		        1,
		        Constants.MAX_COUPON_USAGE
		);

		if (newMaxUsage != null) {
			updated = true;
		    coupon.setMaxUsage(newMaxUsage);
		}
		
		if (updated) {
		    store.save();
		    Logger.info(
		    	    "Coupon updated: "
		    	    + coupon.getCode()
		    	    + " | Discount: "
		    	    + coupon.getDiscountPercentage()
		    	    + "% | Expiration: "
		    	    + coupon.getExpirationDate()
		    	    + " | Max Usage: "
		    	    + coupon.getMaxUsage()
		    	);
		    System.out.println("\n✅ Coupon '" + coupon.getCode() + "' updated successfully.");
		} else {
		    System.out.println("\nℹ️ No changes were made.");
		}

		validator.pause();
		navigation.pop();
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
	
	
	// change status
	private void changeCouponStatus() {
		navigation.push("Change Status");
		navigation.printBreadcrumb();
	    validator.printTitle("CHANGE COUPON STATUS");

	    Coupon coupon = findExistingCoupon();

	    if (coupon == null) {
	    	navigation.pop();
	        return;
	    }

	    System.out.println(
	        "Current Status: "
	        + (coupon.isActive() ? "ACTIVE" : "INACTIVE")
	    );

	    if (!validator.yesOrNo("Change coupon status?")) {
	    	System.out.println("❌ Operation cancelled.");
	    	validator.pause();
	    	navigation.pop();
	    	return;
	    }

	    coupon.toggleStatus();

	    store.save();

	    Logger.info(
	        "Coupon status changed | Code: "
	        + coupon.getCode()
	        + " | New Status: "
	        + (coupon.isActive() ? "ACTIVE" : "INACTIVE")
	    );

	    System.out.println(
	    	    "✅ Coupon status changed to "
	    	    + (coupon.isActive() ? "ACTIVE." : "INACTIVE.")
	    	);

	    validator.pause();
	    navigation.pop();
	}
	
	
	// Delete work flow 
	private void deleteCoupon() {
		navigation.push("Delete Coupon");
		navigation.printBreadcrumb();
		Coupon coupon = findExistingCoupon();

		if (coupon == null) {
			navigation.pop();
		    return;
		}
		System.out.println(coupon);
		if (!validator.yesOrNo("Delete this coupon?")) {
		    System.out.println("❌ Deletion cancelled.");
		    validator.pause();
		    navigation.pop();
		    return;
		}
		store.removeCoupon(coupon);
		store.save();
		System.out.println("✅ Coupon '" + coupon.getCode() + "' deleted successfully.");
		validator.pause();
		navigation.pop();
	}
	
	
	
	
	
	// find a cp beside find cp in store class
	private Coupon findExistingCoupon() {
	    String code = validator.readNonEmptyString("Coupon Code:");
	    return store.findCouponByCode(code);
	}
	
	
	
	
}
