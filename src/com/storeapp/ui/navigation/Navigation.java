package com.storeapp.ui.navigation;

import java.util.ArrayList;
import java.util.List;

public class Navigation {

	
	private final List<String> path = new ArrayList<>();
	
	public Navigation() {
		
	}
	
	
	// push in the list
	public void push(String label) {
		path.add(label);
	}
	
	// pop the elelment (last)
	public void pop() {
		if(!path.isEmpty()) {
			path.remove(path.size()-1);
		}
	}
	
	
	public void printBreadcrumb() {
        if (!path.isEmpty()) {
            System.out.println("📍 " + String.join(" > ", path));
        }
    }
	
}
