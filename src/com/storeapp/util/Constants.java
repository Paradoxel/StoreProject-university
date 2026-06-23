package com.storeapp.util;

import java.time.format.DateTimeFormatter;

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
    
 // Path for the log file
    public static final String LOG_FILE = "store.log";

    // Timestamp format used in log entries
    public static final DateTimeFormatter log_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // for creadit limit
    public static final double CREDIT_LIMIT = 1000000;
}