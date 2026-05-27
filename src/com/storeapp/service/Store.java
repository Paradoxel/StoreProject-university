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
	
	
}
