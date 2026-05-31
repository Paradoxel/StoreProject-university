package com.storeapp.service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.storeapp.model.*;
public class Store implements Serializable {
	// fields
	private List<Product> products;
	private List<Customer> customers;
	private List<Invoice> invoices;
	
	
	public Store() {
	    this.products = new ArrayList<>();
	    this.customers = new ArrayList<>();
	    this.invoices = new ArrayList<>();
	}
	
	
	// Getters
	public List<Product> getProducts(){
		return Collections.unmodifiableList(products);
	}
	
	public List<Customer> getCustomers(){
		return Collections.unmodifiableList(customers);
	}
	
	public List<Invoice> getInvoices(){
		return Collections.unmodifiableList(invoices);
	}
	
	
	// Product management
	public void addProduct(Product product) {
		products.add(product);
	}
	
	public Product findItemByCode(String code) {
		for(Product p:products) {
			if (p.getCode().equals(code))
				return p;
		}
		return null;	
	}
	
	public List<Product> searchItems(String keyword){
		keyword=keyword.toLowerCase();
		List<Product> result=new ArrayList<>();
		for(Product p:products) {
			if(p.getName().toLowerCase().contains(keyword) || p.getCode().toLowerCase().contains(keyword))
				result.add(p);
		}
		return result;
	}
	
	
	// Customer management
	public void addCustomer(Customer customer) {
		customers.add(customer);
	}
	
	public Customer findCustomerByPhone(String phone) {
		for(Customer c:customers) {
			if(c.getPhone().equals(phone))
				return c;
		}
		return null;
	}
	
	
	// Cart management
	public Cart createCart(Customer customer) {
		return new Cart(customer);
	}
	
	public Invoice checkoutCart(Cart cart,PaymentMethod paymentmethod) {
		if(cart.getStatus()==CartStatus.CLOSED)
			throw new IllegalStateException("سبد خرید بسته است.");
		if(cart.getItems().isEmpty())
			throw new IllegalStateException("سبد خرید خالی است.");
			
		Invoice invoice = cart.checkout(paymentmethod);
		
		for(CartItem item:invoice.getItems()) {
			Product product=item.getProduct();
			product.reduceStock(item.getQuantity());
		}
		
		if(paymentmethod==PaymentMethod.CREDIT && invoice.getCustomer() instanceof LoyalCustomer) {
			LoyalCustomer loyal=(LoyalCustomer) invoice.getCustomer();
			loyal.addDebt(invoice.getFinalAmount());
		}
			
		
		
		invoices.add(invoice);
		return invoice;
	}
	
	
	// method for write or read 
	public void saveToFile(String filePath) throws IOException {
	    try (
	        FileOutputStream fileOut = new FileOutputStream(filePath);
	        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)
	    ) {
	        objectOut.writeObject(this);
	    }
	}
	
	public static Store loadFromFile(String filePath) throws IOException, ClassNotFoundException {
	    try (FileInputStream fileIn = new FileInputStream(filePath);
	         ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
	        return (Store) objectIn.readObject();
	    }
	}
	
	

	
	
	

	
	
	
}
