package com.storeapp.service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import com.storeapp.util.Constants;

// Simple log (HAHA)


public class Logger {

    public enum Level {
        DEBUG, INFO, WARNING, ERROR, FATAL
    }

    
     // Main logging method with level
    
    public static void log(Level level, String message) {
        try (FileWriter fw = new FileWriter(Constants.LOG_FILE, true)) {
            String timestamp = LocalDateTime.now().format(Constants.log_fmt);
            String logEntry = String.format("[%s] [%s] %s%n", timestamp, level, message);
            fw.write(logEntry);
            fw.flush();
        } catch (IOException e) {
            System.err.println("❌ Logger error: " + e.getMessage());
            System.out.println("[" + level + "] " + message); // fallback
        }
    }

    // log level methods
    public static void debug(String message) {
        log(Level.DEBUG, message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void warning(String message) {
        log(Level.WARNING, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    public static void fatal(String message) {
        log(Level.FATAL, message);
    }

    // Backward compatibility for old code
    public static void log(String message) {
        info(message);  // default level
    }
}