package com.storeapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
	
	
	
	
	
	
	// Getters
	public String getId() {return id;}
	public List<CartItem> getItems(){
		return Collections.unmodifiableList(items);
	}
	public Customer getCustomer() {
		return customer;
	}
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
	public double getFinalAmount() {
		return finalAmount;
	}
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}
	
}
