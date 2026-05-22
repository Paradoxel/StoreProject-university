package com.storeapp.model;

import java.io.Serializable;

public class CartItem implements Serializable{
	
	// fields
	private Product product;
	private double quantity;
	public CartItem(Product product,double quantity) {
		if(product==null) {throw new IllegalArgumentException("محصول نمیتواند خالی باشد .");}
		if(quantity<=0) {throw new IllegalArgumentException("مقدار باید مثبت باشد .");}
		this.product=product;
		this.quantity=quantity;
	}
	
	// Getters
	public Product getProduct() {return product;}
	public double getQuantity() {return quantity;}
	
	// Setters
	public void setQuantity(double quantity) {
	    if (quantity <= 0) {
	        throw new IllegalArgumentException("مقدار باید مثبت باشد .");
	    }
	    this.quantity = quantity;
	}
	
}
