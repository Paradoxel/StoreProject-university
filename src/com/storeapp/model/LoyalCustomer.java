package com.storeapp.model;

import java.io.Serializable;
import java.time.LocalDate;

public class LoyalCustomer extends Customer implements Serializable {
	// fields + customer fields
	private String membershipCode;
	private LocalDate joinDate;
	// amount the store owes to the customer
	private double credit;
	// amount the customer owes to the store
	private double debt;
	
	// Constructor
	public LoyalCustomer(String name, String phone, String membershipCode, LocalDate joinDate) {
		super(name,phone);
		this.membershipCode = membershipCode;
        this.joinDate = joinDate;
        this.credit = 0.0;
        this.debt = 0.0;
	}
	
	
	// Getters
	public String getMembershipCode() {return membershipCode;}
	public LocalDate getJoinDate() {return joinDate;}
	public double getCredit() {return credit;}
	public double getDebt() {return debt;}
	
	@Override
	public boolean canBuyOnCredit() {
		return debt<1000000;
	}
	
	@Override
	public boolean canReturnItem() {
		return true;
	}
	
	
	public void addDebt(double amount) {
		if (amount>0) {
			debt+=amount;
		}
	}
	
	public void addCredit(double amount) {
		if (amount>0) {
			credit+=amount;
		}
	}
	
	
	
	
}
