package com.storeapp.model;

import java.io.Serializable;
// Represents a invoice 
public class Invoice implements Serializable {
	private final Cart cart;
	private final PaymentMethod paymentmethod;
	
	public Invoice(Cart cart,PaymentMethod paymentMethod) {
		this.cart=cart;
		this.paymentmethod=paymentMethod;
	}
	
}
