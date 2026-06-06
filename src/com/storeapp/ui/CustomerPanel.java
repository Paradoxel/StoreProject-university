package com.storeapp.ui;

import com.storeapp.service.Store;
import com.storeapp.util.InputValidator;
import com.storeapp.model.*;
public class CustomerPanel {
	private Store store;
	private InputValidator validator;
	public CustomerPanel(Store store,InputValidator validator) {
		this.store=store;
		this.validator=validator;
	}

}
