package com.storeapp.model;

import java.io.Serializable;
// Represents a customer in the store
public class Customer implements Serializable {
	// fields
	private String name;
	private String phone;
	
	// Constructor
	public Customer(String name,String phone) {
		this.name=name;
		this.phone=phone;
	}
	

	// Getters
	public String getName() {return name;}
	public String getPhone() {return phone;}
	
	// Setters
	public void setName(String name) {this.name=name;}
	public void setPhone(String phone) {this.phone=phone;}
	
	// Regular  customers cannot buy on credit
	public boolean canBuyOnCredit(double purchaseAmount) {return false;}
	
	// Regular customers cannot return items
	public boolean canReturnItem() {return false;}
	
	
}
