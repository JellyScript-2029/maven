package com.maven.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//This class handles all JSON file 
public class FileManager {
    private static final Gson gson = new GsonBuilder()
            //setPrettyPrinting() makes the saved JSON human-readable
            .setPrettyPrinting()
            .create();

    // Lock for synchronized file access
    // thread safe is wrapped in synchronized(fileLock)
    private static final Object fileLock = new Object();

    // Load a list from JSON file
    public static <T> ArrayList<T> loadListFromJson(String filePath, Type typeOfList) {
        synchronized (fileLock) { // Only one thread enters at a time
            File file = new File(filePath);

            // Check if file exists
            if (!file.exists()) {
                System.out.println("[WARNING] File not found: " + filePath);
                return new ArrayList<>();
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                ArrayList<T> result = gson.fromJson(reader, typeOfList);

                if (result == null) {
                    System.out.println("[WARNING] JSON file is empty: " + filePath);
                    return new ArrayList<>();
                }

                System.out.println("[SUCCESS] Loaded " + result.size() + " records from " + filePath);
                return result;

            } catch (FileNotFoundException e) {
                System.out.println("[ERROR] File not found: " + filePath);
                return new ArrayList<>();
            } catch (IOException e) {
                System.out.println("[ERROR] Failed to read " + filePath + ": " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            } catch (com.google.gson.JsonSyntaxException e) {
                System.out.println("[ERROR] Invalid JSON syntax in " + filePath + ": " + e.getMessage());
                return new ArrayList<>();
            }
        }
    }

    // Save a list to JSON file with directory creation
    public static <T> void saveListToJson(String filePath, List<T> list) {
        synchronized (fileLock) {
            try {
                File file = new File(filePath);

                // Create parent folders if they do not exist yet
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    boolean dirsCreated = file.getParentFile().mkdirs();
                    if (!dirsCreated) {
                        System.out.println("[WARNING] Could not create directory for: " + filePath);
                    }
                }

                // Write JSON to file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    gson.toJson(list, writer); // Serialize list to JSON to file
                    System.out.println("[SUCCESS] Saved " + list.size() + " records to " + filePath);
                }

            } catch (IOException e) {
                System.out.println("[ERROR] Failed to save to " + filePath + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Append receipt to receipt file (SYNCHRONIZED for thread safety)
    public static synchronized void appendReceipt(String receiptText) {
        try {
            File file = new File("receipt.txt");

            try (FileWriter fw = new FileWriter(file, true);
                    BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(receiptText);
                bw.newLine();
                // Visual separator between receipts
                bw.write(
                        "════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
                bw.newLine();
                System.out.println("[SUCCESS] Receipt saved to receipt.txt");
            }

        } catch (IOException e) {
            System.out.println("[ERROR] Failed to append receipt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Save single object to JSON file
    public static <T> void saveObjectToJson(String filePath, T object) {
        synchronized (fileLock) {
            try {
                File file = new File(filePath);

                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    gson.toJson(object, writer);
                    System.out.println("[SUCCESS] Saved object to " + filePath);
                }

            } catch (IOException e) {
                System.out.println("[ERROR] Failed to save object to " + filePath + ": " + e.getMessage());
            }
        }
    }

    // Load single object from JSON file
    public static <T> T loadObjectFromJson(String filePath, Class<T> classOfT) {
        synchronized (fileLock) {
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("[WARNING] File not found: " + filePath);
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return gson.fromJson(reader, classOfT);

            } catch (IOException e) {
                System.out.println("[ERROR] Failed to read " + filePath + ": " + e.getMessage());
                return null;
            }
        }
    }

    // Returns true if the file exists on disk
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    // Returns file size in bytes (0 if not found)
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.length();
        }
        return 0;
    }
}
