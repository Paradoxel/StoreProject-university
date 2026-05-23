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
	
	
	
	// important method
	public void addItem(Product product,double quantity) {
		// check status of cart
		if(status==CartStatus.CLOSED)
			throw new IllegalStateException ("سفید خرید بسته است نمیتوانید محصولی اضافه یا کم کنید .");
		
		// check value of quantity
		if (quantity<=0) {
			throw new IllegalArgumentException("تعداد محصول باید مقداری مثبت باشد.");
		}

		for(CartItem item : items) {
				if(item.getProduct().getCode().equals(product.getCode())) {
					
					item.setQuantity(item.getQuantity()+quantity);
					return ; // exit from method
				}	
		}
		
		
		CartItem cartitem=new CartItem(product,quantity);
		items.add(cartitem);
	}
	
	public void removeItem(String code) {
		if(status==CartStatus.CLOSED)
			throw new IllegalStateException ("سفید خرید بسته است نمیتوانید محصولی اضافه یا کم کنید .");
		for(CartItem item:items) {
			if(item.getProduct().getCode().equals(code)) {
				items.remove(item);
				return;
			}
		}
	}
	
	
	
	
	
	
	
	
}
