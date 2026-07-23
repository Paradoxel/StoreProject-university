package com.storeapp.ui.navigation;

import java.util.ArrayDeque;
import java.util.Deque;

public class Navigation {

    private final Deque<String> stack;

    public Navigation() {
        this.stack = new ArrayDeque<>();
    }

    public void push(String page) {
        stack.push(page);
    }

    public String pop() {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.pop();
    }

    public String peek() {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public void clear() {
        stack.clear();
    }
}