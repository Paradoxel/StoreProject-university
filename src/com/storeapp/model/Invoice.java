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
	
	
	
	
	// showing the object 
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();

	    sb.append("═══════════════════════════════════\n");
	    sb.append("🧾 Official Store Invoice\n");
	    sb.append("═══════════════════════════════════\n");
	    sb.append("Invoice # : ").append(id).append("\n");
	    sb.append("Date      : ").append(dateTime).append("\n");
	    sb.append("Customer  : ").append(customer.getName()).append("\n");
	    sb.append("Phone     : ").append(customer.getPhone()).append("\n");

	    if (customer instanceof LoyalCustomer) {
	        LoyalCustomer loyal = (LoyalCustomer) customer;
	        sb.append("Member Code : ").append(loyal.getMembershipCode()).append("\n");
	    }

	    sb.append("───────────────────────────────────\n");
	    sb.append("Purchased Items :\n");

	    for (CartItem item : items) {
	        sb.append("  - ")
	          .append(item.getProduct().getName())
	          .append(" (")
	          .append(item.getQuantity())
	          .append(" ")
	          .append(item.getProduct().getUnitType())
	          .append(") = ")
	          .append(String.format("%.2f", item.getTotalPrice()))  // formatted to avoid scientific notation
	          .append(" Tomans\n");
	    }

	    sb.append("───────────────────────────────────\n");
	    sb.append("Total Amount : ").append(String.format("%.2f", finalAmount)).append(" Tomans\n");
	    sb.append("Payment     : ")
	      .append(paymentMethod == PaymentMethod.CASH ? "Cash" : "Credit")
	      .append("\n");
	    sb.append("═══════════════════════════════════\n");

	    return sb.toString();
	}
	
}
