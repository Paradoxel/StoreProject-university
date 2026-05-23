package com.storeapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
	
}
