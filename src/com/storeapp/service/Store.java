package com.storeapp.service;

import java.io.Serializable;
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
	
	
	public void addCustomer(Customer customer) {
		customers.add(customer);
	}
	
	public Customer findCustomerByPhone(String phone) {
		for(Customer c:customers) {
			if(c.getPhone().equals(c))
				return c;
		}
		return null;
	}
	
	
	
}
