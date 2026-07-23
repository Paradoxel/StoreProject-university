package com.storeapp.ui.navigation;

import java.awt.Taskbar.State;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
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

	    List<String> pages = new ArrayList<>(stack);
	    Collections.reverse(pages);

	    return String.join(" > ", pages);
	}
       
}