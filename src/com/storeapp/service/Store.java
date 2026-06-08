package com.storeapp.service;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.storeapp.model.*;
public class Store implements Serializable {
	// fields
	private List<Product> products;
	private List<Customer> customers;
	private List<Invoice> invoices;
	// hash map for return item safe(key pair value)
	private Map<String, Double> returnedQuantities = new HashMap<>();
	
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
	
	public Customer findLoyalCustomerByCode (String code) {
		for(Customer c:customers) {
			if(c instanceof LoyalCustomer) {
				if(((LoyalCustomer) c).getMembershipCode().equals(code))
					return c;
			}
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
	
	
	public boolean isMembershipCodeTaken(String code) {
		for(Customer c: customers) {
			if(c instanceof LoyalCustomer) {
				if(((LoyalCustomer) c).getMembershipCode().equals(code)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public String generateMembershipCode(String name) {
		// Step 1: Create a numeric hash from the customer's name.
		long nameHash=0;
		for(int i=0;i<name.length();i++) {
			nameHash+=(long)name.charAt(i)*(i+1);
		}
		// Step 2: Get the current system time in milliseconds
		long timestamp=System.currentTimeMillis();
		// Step 3: A well-known magic number used in hash functions (decimal 2654435769).
		long magic=0x9E3779B9L;
		// to produce a hard-to-guess, unique numeric seed.
		nameHash = nameHash & magic;   // step A: AND with magic number
		nameHash = nameHash ^ timestamp;   // step B: XOR with current timestamp
		Random random = new Random();
		nameHash = nameHash | (random.nextInt(256) << 16);   // step C: OR with random bits
		nameHash = nameHash ^ (magic >>> 16);   // step D: XOR with magic right-shifted
		nameHash= Math.abs(nameHash) & 0xFFFFFFFFL;// Step E: ensure a positive 32‑bit number
		// Step 4: ensure uniqueness – keep trying next numbers if the code is already taken
		String code;
		do {
		    code = String.format("%08X", nameHash);
		    nameHash = (nameHash + 1) & 0xFFFFFFFFL;
		} while (isMembershipCodeTaken(code));

		return code;

	}
	
	
	public void removeProduct(Product product) {
	    products.remove(product);
	}
	
	
	// Generate a unique code for a product like PRD-1
	public String generateProductCode() {
		int nextNumber = products.size() + 1;
		String code;
		do {
			code="PRD-"+(nextNumber);
			nextNumber++;
		}while(findItemByCode(code)!=null);
		return code;
	}
	
	// find an invoice by its unique  id
	public Invoice findInvoiceById(String id) {
		for(Invoice inv : invoices) {
			if (inv.getId().equals(id)) {
				return inv;
			}
		}
		return null;
	}
	
	// Processes a return an item for a loyal customer
	public void processReturn(LoyalCustomer lc,Invoice inv,String productCode,double quantity) {
		CartItem target = null;
	    for (CartItem ci : inv.getItems()) {
	        if (ci.getProduct().getCode().equals(productCode)) {
	            target = ci;
	            break;
	        }
	    }
	    if (target == null) {
	        throw new IllegalArgumentException("Product not in this invoice.");
	    }
	    if (quantity <= 0 || quantity > target.getQuantity()) {
	        throw new IllegalArgumentException("Invalid quantity.");
	    }
	    // build the uniqu key for this invoice item 
	    String returnKey=inv.getId()+":"+target.getProduct().getCode();
	    // How many of this item have already been returned?
	    double alreadyReturned=returnedQuantities.getOrDefault(returnKey, 0.0);
	    // Calculate total
	    double totalAfterThis=alreadyReturned+quantity;
	    // check if exist
	    if (totalAfterThis > target.getQuantity()) {
	        throw new IllegalArgumentException(
	            "Cannot return more than purchased. Already returned: " + alreadyReturned + ", Purchased: " + target.getQuantity());
	    }
	 // 1. if valid --> Increase stock 
	    returnedQuantities.put(returnKey, totalAfterThis);
	    target.getProduct().increaseStock(quantity);
	    double refund = target.getProduct().getDiscountedPrice() * quantity;
	    lc.addCredit(refund);
	}
	
	
	
	
	

	
	
	

	
	
	
}
