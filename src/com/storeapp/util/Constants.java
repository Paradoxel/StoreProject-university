package com.storeapp.util;

import java.time.format.DateTimeFormatter;

/**
 * Central place for all fixed values used in the application.
 * Improved for final version.
 */
public class Constants {

    // Prevent instantiation
    private Constants() {}

    /** The secret code for admin login. */
    public static final String ADMIN_CODE = "admin123";

    /** The file path where the store data is saved. */
    public static final String STORE_FILE = "store.dat";
   
    // Sample data for random generation (improved and expanded)
    public static final String[] SAMPLE_NAMES = {
         "Milk", "Rice", "Oil", "Cheese", "Butter", "Bread", "Apple", "Banana", 
         "Chicken", "Beef", "Eggs", "Yogurt", "Tomato", "Potato", "Onion", 
         "Orange", "Grape", "Watermelon", "Fish", "Lamb"
    };
   
    public static final String[] MANUFACTURERS = {
        "Ali", "Mohmmadreza", "Mehdi", "Reza", "Hamid", "Rahim", 
        "Yasin", "Mohammad", "Abtin", "Houman", "Sina", "Parsa", 
        "Nima", "Kian", "Pouria"
    };
    
    public static final String[] COLORS = {
         "Red", "Blue", "White", "Yellow", "Green", "Black", 
         "Orange", "Purple", "Pink", "Brown", "Gray"
    };
   
    // Path for the log file
    public static final String LOG_FILE = "store.log";
    
    // Timestamp format used in log entries
    public static final DateTimeFormatter log_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   
    // Credit limit
    public static final double CREDIT_LIMIT = 1000000;
   
    // Crypto
    public static final String SECRET_KEY = "InvoiceKey2026!!";
   
    public static final String SEPARATOR = "#";
   
    // saving store file
    public static final String STORE_ENCRYPTION_KEY =
         "9K$wL2@pQ#8xVm!R7cF&nA5zHsT1uYdE";
   
   
    public static final DateTimeFormatter DISPLAY_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
}