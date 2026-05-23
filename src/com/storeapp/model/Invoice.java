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
	    sb.append("🧾 فاکتور رسمی فروشگاه\n");
	    sb.append("═══════════════════════════════════\n");
	    sb.append("🔢 شماره فاکتور : ").append(id).append("\n");
	    sb.append("📅 تاریخ        : ").append(dateTime).append("\n");
	    sb.append("👤 مشتری        : ").append(customer.getName()).append("\n");
	    sb.append("📞 تلفن         : ").append(customer.getPhone()).append("\n");
	    
	    if (customer instanceof LoyalCustomer) {
	        LoyalCustomer loyal = (LoyalCustomer) customer;
	        sb.append("⭐ کد عضویت     : ").append(loyal.getMembershipCode()).append("\n");
	    }
	    
	    sb.append("───────────────────────────────────\n");
	    sb.append("🛒 اقلام خریداری‌شده :\n");
	    
	    for (CartItem item : items) {
	        sb.append("  ◻ ")
	          .append(item.getProduct().getName())
	          .append(" (")
	          .append(item.getQuantity())
	          .append(" ")
	          .append(item.getProduct().getUnitType())
	          .append(") = ")
	          .append(item.getTotalPrice())
	          .append(" ریال\n");
	    }
	    
	    sb.append("───────────────────────────────────\n");
	    sb.append("💰 مبلغ کل       : ").append(finalAmount).append(" ریال\n");
	    sb.append("💳 نحوه پرداخت   : ")
	      .append(paymentMethod == PaymentMethod.CASH ? "نقدی" : "اعتباری")
	      .append("\n");
	    sb.append("═══════════════════════════════════\n");
	    
	    return sb.toString();
	}
	
}
