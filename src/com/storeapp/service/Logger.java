package com.storeapp.service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import com.storeapp.util.Constants;

/**
 * Simple logger – writes timestamped messages to store.log.
 */
public class Logger {

    public static void log(String message) {
        // open log file in append mode, auto-closed by try-with-resources
        try (FileWriter fw = new FileWriter(Constants.LOG_FILE, true)) {
            String timestamp = LocalDateTime.now().format(Constants.log_fmt);
            fw.write("[" + timestamp + "] " + message + "\n");
        } catch (IOException e) {
            System.err.println("❌ Logger error: " + e.getMessage());
        }
    }
}