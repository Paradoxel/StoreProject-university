package com.storeapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	
	
	
	
	// showing the object 
	@Override
	public String toString() {
	    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm");
	    StringBuilder sb = new StringBuilder();

	    sb.append("══════════════════════════════════════════════════════\n");
	    sb.append("               🧾 OFFICIAL INVOICE                    \n");
	    sb.append("══════════════════════════════════════════════════════\n");
	    sb.append(" Invoice # : ").append(id).append("\n");
	    sb.append(" Date      : ").append(dateTime.format(fmt)).append("\n");
	    sb.append(" Customer  : ").append(customer.getName()).append("\n");
	    sb.append(" Phone     : ").append(customer.getPhone()).append("\n");
	    sb.append("──────────────────────────────────────────────────────\n");

	 
	    sb.append(String.format(" %-24s %-6s %-8s %-12s %-14s%n", "Item", "Qty", "Unit", "Price", "Subtotal"));
	    sb.append(" ------------------------ ------ -------- ------------ --------------\n");

	    for (CartItem item : items) {
	        Product p = item.getProduct();
	        sb.append(String.format(" %-24s %-6.1f %-8s %-12.2f %-14.2f%n",
	                p.getName(),
	                item.getQuantity(),
	                p.getUnitType(),
	                p.getDiscountedPrice(),
	                item.getTotalPrice()));
	    }

	    sb.append(" ------------------------ ------ -------- ------------ --------------\n");
	    sb.append(String.format(" TOTAL AMOUNT : %.2f Tomans%n", finalAmount));
	    sb.append(" PAYMENT      : ").append(paymentMethod == PaymentMethod.CASH ? "Cash" : "Credit").append("\n");
	    sb.append("══════════════════════════════════════════════════════\n");

	    return sb.toString();
	}
	
}
