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
	
	
	
}
