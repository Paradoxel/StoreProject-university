package com.storeapp;

import java.util.Scanner;

import com.storeapp.service.Logger;
import com.storeapp.ui.ConsoleUI;

public class Main {
	public static void main(String[]args) {
		Logger.log("Application started");
		Scanner scanner=new Scanner(System.in);
		ConsoleUI ui = new ConsoleUI(scanner);
        ui.start();
        scanner.close();
        Logger.log("Application terminated");
	}
}
