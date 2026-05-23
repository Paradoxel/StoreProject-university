package com.storeapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
// Represents a invoice 
import java.util.List;
public class Invoice implements Serializable {
	private final PaymentMethod paymentMethod;
	private final String id;
	private final List<CartItem> items;
	private final Customer customer;
	private final LocalDateTime dateTime;
	private final double finalAmount;
	
	public Invoice(Cart cart,PaymentMethod paymentMethod) {
		this.paymentMethod=paymentMethod;
		id = "INV-" + System.currentTimeMillis();
		// for security a copy of that
		items=new ArrayList<>(cart.getItems()) ;
		customer=cart.getCustomer();
		dateTime=LocalDateTime.now();
		finalAmount=cart.getTotalAmount();	

	}
	
}
