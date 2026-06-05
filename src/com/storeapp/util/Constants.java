package com.storeapp.util;

/**
 * Central place for all fixed values used in the application.
 */
public class Constants {

    // Prevent instantiation
    private Constants() {}

    /** The secret code for admin login. */
    public static final String ADMIN_CODE = "admin123";

    /** The file path where the store data is saved. */
    public static final String STORE_FILE = "store.dat";
    
    // for generate random in panel admin
    public static final String[] SAMPLE_NAMES=  {
    	    "Milk", "Rice", "Oil", "Cheese", "Butter", "Bread", "Apple", "Banana", "Chicken", "Beef"
    };
    
    public static final String[] MANUFACTURERS = {"Ali","Mohmmadreza","Mehdi","Reza","Hamid","Rahim","Yasin","Mohammad","Abtin","Houman"};
    public static final String[] COLORS={
    	    "Red", "Blue", "White", "Yellow", "Green", "Black"
    };
}