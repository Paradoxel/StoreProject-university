package com.storeapp;

import java.util.Scanner;

import com.storeapp.ui.ConsoleUI;

public class Main {
	public static void main(String[]args) {
		Scanner scanner=new Scanner(System.in);
		ConsoleUI ui = new ConsoleUI(scanner);
        ui.start();
        scanner.close();
	}
}
