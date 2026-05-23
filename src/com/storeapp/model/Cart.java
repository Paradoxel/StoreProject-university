package com.storeapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart implements Serializable {
	
	// fields
	private List<CartItem> items;
	private Customer customer;
	private LocalDateTime createdDate;
	private CartStatus status;
	
	
	// Constructor
	public Cart(Customer customer) {
		this.customer=customer;
		items=new ArrayList<>();
		createdDate=LocalDateTime.now();
		status=CartStatus.OPEN;
	}
	
	// getters 
	public List<CartItem> getItems(){
		/* for the security
		 return read_only view of the items list
		 Encapsulation: external callers cannot modify the cart directly
		 */
		return Collections.unmodifiableList(items);
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	
	public CartStatus getStatus() {
		return status;
	}
	
}
