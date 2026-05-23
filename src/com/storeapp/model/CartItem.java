package com.storeapp.model;

import java.io.Serializable;
import java.util.Objects;
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
	
	// Gettersُ
	public Product getProduct() {return product;}
	public double getQuantity() {return quantity;}
	
	// Setters
	public void setQuantity(double quantity) {
	    if (quantity <= 0) {
	        throw new IllegalArgumentException("مقدار باید مثبت باشد .");
	    }
	    this.quantity = quantity;
	}
	
	
	// Get Total Price
	public double getTotalPrice() {
		return product.getDiscountedPrice() * quantity;

	}
	
	// override equals
	@Override
	public boolean equals(Object obj) {
		if(this==obj) return true;
		if(obj==null || obj instanceof  CartItem)return false;
		CartItem obj_open=(CartItem) obj;
		return this.product.getCode().equals(obj_open.product.getCode());
	}
	
	// override hash code
	@Override
	public int hashCode() {
	    return Objects.hash(product.getCode());
	}
	

	
}
