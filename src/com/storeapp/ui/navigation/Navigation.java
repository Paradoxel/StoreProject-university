package com.storeapp.ui.navigation;

import java.util.Stack;

public class Navigation {

	private final Stack<String> stack;

	public Navigation() {
	    stack = new Stack<>();
	}

	public void push(String label) {
	    stack.push(label);
	}

	public void pop() {
	    if (!stack.isEmpty()) {
	        stack.pop();
	    }
	}

	public String peek() {
	    if (stack.isEmpty()) {
	        return null;
	    }
	    return stack.peek();
	}

	public void clear() {
	    stack.clear();
	}

	public String getBreadcrumb() {

	    if (stack.isEmpty()) {
	        return "";
	    }

	    return String.join(" > ", stack);
	}
	
	
	public void printBreadcrumb() {
	    System.out.println("📍 " + getBreadcrumb());
	}
       
}