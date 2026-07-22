package com.storeapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
// Represents a invoice 
import java.util.List;

import com.storeapp.service.CryptoService;
import com.storeapp.util.Constants;
public class Invoice implements Serializable {
	private final PaymentMethod paymentMethod;
	private final String id;
	private final List<CartItem> items;
	private final Customer customer;
	private final LocalDateTime dateTime;
	private final double finalAmount;

	public Invoice(Cart cart,PaymentMethod paymentMethod,double finalAmount) {
		this.paymentMethod = paymentMethod;
		items = new ArrayList<>(cart.getItems());
		customer = cart.getCustomer();
		dateTime = LocalDateTime.now();
		this.finalAmount = finalAmount;
		id = CryptoService.encrypt(buildInvoiceData());

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
	
	
	private String buildInvoiceData() {
	    return String.join(
	            Constants.SEPARATOR,
	            customer.getPhone(),
	            dateTime.toString(),
	            String.valueOf(finalAmount)
	    );
	}
	
	
	// showing the object 
	@Override
	public String toString() {

	    double originalTotal = 0;
	    double productDiscount = 0;

	    DateTimeFormatter fmt =
	            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	    StringBuilder sb = new StringBuilder();

	    sb.append("════════════════════════════════════════════════════════════════════════════\n");
	    sb.append("                           🧾 OFFICIAL INVOICE\n");
	    sb.append("════════════════════════════════════════════════════════════════════════════\n");

	    sb.append(" Invoice Token : ").append(id).append('\n');
	    sb.append(" Date          : ").append(dateTime.format(fmt)).append('\n');
	    sb.append(" Customer      : ").append(customer.getName()).append('\n');
	    sb.append(" Phone         : ").append(customer.getPhone()).append('\n');

	    sb.append("────────────────────────────────────────────────────────────────────────────\n");

	    sb.append(String.format(
	            "%-20s %-6s %-8s %12s %8s %12s %14s%n",
	            "Item",
	            "Qty",
	            "Unit",
	            "Price",
	            "Disc",
	            "Final",
	            "Subtotal"));

	    sb.append("────────────────────────────────────────────────────────────────────────────\n");

	    for (CartItem item : items) {

	        Product p = item.getProduct();

	        String qtyStr =
	                item.getQuantity() == Math.floor(item.getQuantity())
	                        ? String.valueOf((long) item.getQuantity())
	                        : String.format("%.1f", item.getQuantity());

	        originalTotal += item.getOriginalTotalPrice();
	        productDiscount += item.getDiscountAmount();

	        String discount =
	                p.getDiscountPercent() > 0
	                        ? String.format("%.0f%%", p.getDiscountPercent())
	                        : "-";

	        sb.append(String.format(
	                "%-20s %-6s %-8s %,12d %8s %,12d %,14d%n",
	                p.getName(),
	                qtyStr,
	                p.getUnitType(),
	                (long) p.getPrice(),
	                discount,
	                (long) item.getFinalUnitPrice(),
	                (long) item.getTotalPrice()
	        ));
	    }

	    double couponDiscount =
	            (originalTotal - productDiscount) - finalAmount;

	    if (couponDiscount < 0)
	        couponDiscount = 0;

	    sb.append("────────────────────────────────────────────────────────────────────────────\n");

	    sb.append(String.format(
	            "%-20s %,22d Tomans%n",
	            "Original Total:",
	            (long) originalTotal));

	    sb.append(String.format(
	            "%-20s %,22d Tomans%n",
	            "Product Discount:",
	            (long) productDiscount));

	    sb.append(String.format(
	            "%-20s %,22d Tomans%n",
	            "Coupon Discount:",
	            (long) couponDiscount));

	    sb.append("────────────────────────────────────────────────────────────────────────────\n");

	    sb.append(String.format(
	            "%-20s %,22d Tomans%n",
	            "Final Total:",
	            (long) finalAmount));

	    sb.append(String.format(
	            "%-20s %s%n",
	            "Payment:",
	            paymentMethod));

	    sb.append("════════════════════════════════════════════════════════════════════════════\n");

	    return sb.toString();
	}
	
}
