package com.maven.service;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
 
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maven.model.Product;
 
//This is the program where the customer runs
public class Client {
 
    // ANSI COLOR CODES
    static final String RESET = "\u001B[0m";
    static final String CYAN = "\u001B[36m";
    static final String YELLOW = "\u001B[33m";
    static final String B_CYAN = "\u001B[1;36m";
    static final String B_YELLOW = "\u001B[1;33m";
    static final String B_GREEN = "\u001B[1;32m";
    static final String B_WHITE = "\u001B[1;37m";
    static final String B_RED = "\u001B[1;31m";
    
    //tracks the customer just finished a payment 
    private static boolean paymentJustCompleted = false;
    
    //entry point
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Gson gson = new Gson();
        
        //open tcp socket connection to the server on localhost 8080

        try (Socket socket = new Socket("localhost", Server.PORT); //try-with-resources to ensure the sockete is closed automatically
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
 
            System.out.println(B_GREEN + "╔════════════════════════════════════════╗" + RESET);
            System.out.println(B_GREEN + "║ Connected to JJ Store System! SUCCESS  ║" + RESET);
            System.out.println(B_GREEN + "╚════════════════════════════════════════╝" + RESET);
            
            //main shopping loop
            while (true) {
 
                // server sends the full product list as a JSON String everytime this loop starts
                String rawInventoryJson = in.readLine();
                if (rawInventoryJson == null) {
                    break; // Server closed connection
                }
                
                //Deserialize JSON → ArrayList<Product>
                ArrayList<Product> productInventory = gson.fromJson(rawInventoryJson,
                        new TypeToken<ArrayList<Product>>() {
                        }.getType());
 
                // Ask if customer wants to buy again (show only after a payment)
                if (paymentJustCompleted) {
                    paymentJustCompleted = false;
                    System.out.println(B_CYAN + "\n╔════════════════════════════════════════════╗");
                    System.out.println("║   Would you like to buy again? (yes/no)    ║");
                    System.out.println("╚════════════════════════════════════════════╝" + RESET);
                    System.out.print(" >>> ");
 
                    String again = scanner.nextLine().trim().toLowerCase();
                    if (!again.equals("yes") && !again.equals("y")) {
                        // if customer is done tell server to close the session
                        out.println("CANCEL");
                        System.out.println(B_GREEN + "\n╔════════════════════════════════════════╗");
                        System.out.println("║  Thank you for shopping at JJ Store!   ║");
                        System.out.println("║  See you next time! Goodbye.           ║");
                        System.out.println("╚════════════════════════════════════════╝" + RESET);
                        break;
                    }
                    // If yes, Continue to display inventory again
                }
 
                // Display current inventory
                displayInventoryScreen(productInventory);
 
                System.out.print(" >>> Please enter selection number: ");
                String choice = scanner.nextLine().trim();
 
                if (choice.equals("2")) {
                    //customer choose to exit
                    out.println("CANCEL");
                    System.out.println(B_GREEN + "\nThank you for using JJ Store!" + RESET);
                    break;
                } else if (choice.equals("1")) {
                    //customer choose to buy
                    out.println("BUY"); // tell server client are buying

                    //ask product deatils, product id and quantity
                    System.out.print("Enter product ID: ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Enter quantity: ");
                    String qty = scanner.nextLine().trim();
                    
                    //send to server
                    out.println(id); // product id
                    out.println(qty); // how many products to buy
                    
                    // Wait for server's initial stock check response
                    String initialCheck = in.readLine();
                    if (initialCheck == null || initialCheck.startsWith("FAILED")) {
                        System.out.println(
                                B_RED + "\n[ERROR]: " + (initialCheck != null ? initialCheck : "Server error") + RESET);
                        continue; // Loop back to main menu
                    }
 
                    // Process checkout
                    processCheckout(scanner, in, out); // if item accepted then proceed to checkout menu
                    System.out.println(B_RED + "[ERROR]: Invalid selection. Please enter 1 or 2." + RESET);

                }
            }
        } catch (IOException e) {
            System.out.println(B_RED + "[ERROR] Connection lost: " + e.getMessage() + RESET);
        } finally {
            System.out.println(B_CYAN + "\nDisconnected from JJ Store Server." + RESET);
        }
    }
 
    // Display the main inventory screen grouped by category
    private static void displayInventoryScreen(ArrayList<Product> productInventory) {
        System.out.println(CYAN + "╔══════════════════════════════════════════════════════════════════╗");
        System.out.printf("║                          " + B_WHITE + "%-40s" + CYAN + "║%n", "CURRENT INVENTORY");
        printFormattedInventory(productInventory); // Print category tables
        System.out.println(CYAN + "╠══════════════════════════════════════════════════════════════════╣");
        String invOptions = "[1] Buy Products                  [2] Cancel / Exit";
        System.out.printf(CYAN + "║     " + B_WHITE + "%-61s" + CYAN + "║%n", invOptions);
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════════════════╝" + RESET);
    }
 
    // Main checkout loop, this handles add items, pay, or cancel
    private static void processCheckout(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        boolean transactionActive = true;
 
        while (transactionActive) {
            //server sends the current running total
            String currentBillTotal = in.readLine();
            if (currentBillTotal == null) {
                break;
            }
 
            displayCheckoutScreen(currentBillTotal);
            System.out.print(" >>> Select transaction option: ");
            String loopChoice = scanner.nextLine().trim();
            out.println(loopChoice); // send choice to server
 
            switch (loopChoice) {
                case "1" -> { // Add more products
                    System.out.print("Enter next product ID: ");
                    String nextId = scanner.nextLine().trim();
                    out.println(nextId);
 
                    System.out.print("Enter quantity: ");
                    String nextQty = scanner.nextLine().trim();
                    out.println(nextQty);
 
                    String result = in.readLine(); // Server replies SUCCESS or FAILED
                    if (result == null) {
                        System.out.println(B_RED + "[ERROR]: Server disconnected" + RESET);
                        transactionActive = false;
                    } else if (result.equals("SUCCESS")) {
                        System.out.println(B_GREEN + "\n[Added] Item added to cart." + RESET);
                    } else {
                        System.out.println(B_RED + "\n[ERROR]: Failed to add product. Check ID or stock." + RESET);
                    }
                }
 
                case "2" -> {  // Proceed to payment
                    boolean paid = processPayment(scanner, in, out);
                    if (paid) {
                        transactionActive = false;
                    }
                }
 
                case "3" -> { // Cancel transaction
                    System.out.print(B_YELLOW + "Are you sure you want to cancel? (yes/no): " + RESET);
                    String conf = scanner.nextLine().trim();
                    out.println(conf); // send confirmation to server
                    if (conf.equalsIgnoreCase("yes") || conf.equalsIgnoreCase("y")) {
                        System.out.println(B_YELLOW + "[!] Transaction cancelled." + RESET);
                        transactionActive = false;
                    }
                }
 
                default -> System.out.println(B_RED + "[ERROR]: Invalid choice. Please enter 1, 2, or 3." + RESET);
            }
        }
    }
 
    // Display the checkout screen with current total bill
    private static void displayCheckoutScreen(String currentBillTotal) {
        System.out.println(CYAN + "\n╔═════════════════════════════════════════════════════════════════════════╗");
        String billText = String.format("TOTAL CURRENT BILL: PHP %s", currentBillTotal);
        System.out.printf(CYAN + "║ " + B_WHITE + "  %-68s" + CYAN + "  ║%n", billText);
        System.out.println(CYAN + "╠═════════════════════════════════════════════════════════════════════════╣");
        String optionsText = "[1] Add Product         [2] Proceed to Pay         [3] Cancel";
        System.out.printf(CYAN + "║ " + B_WHITE + "    %-66s" + CYAN + "  ║%n", optionsText);
        System.out
                .println(CYAN + "╚═════════════════════════════════════════════════════════════════════════╝" + RESET);
    }
 
    // Handle payment processing (cash or card)
    private static boolean processPayment(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        while (true) {
            displayPaymentMenu();
            System.out.print(" >>> Enter method number: ");
            String payChoice = scanner.nextLine().trim();
 
            switch (payChoice) {
                case "1": // Cash payment
                    return processCashPayment(scanner, in, out);
 
                case "2": // Card payment - show login/register menu first
                    return processCardAuthAndPayment(scanner, in, out);
 
                case "3": // Return to cart
                    System.out.println(B_YELLOW + "[!] Returning to cart..." + RESET);
                    out.println("3"); // tel server to stay in checkout
                    return false;
 
                default:
                    System.out.println(B_RED + "[ERROR]: Invalid selection. Please enter 1, 2, or 3." + RESET);
            }
        }
    }
 
    // Process cash payment
    private static boolean processCashPayment(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        out.println("1"); //tell server cash payment is chosen
        System.out.print("Enter cash amount (PHP): ");
        String cashAmount = scanner.nextLine().trim();
        out.println(cashAmount);
 
        String status = in.readLine(); // receives a result
        if (status == null) {
            System.out.println(B_RED + "[ERROR]: Server disconnected" + RESET);
            return false;
        }
 
        if (status.equals("PAYMENT_SUCCESS")) {
            System.out.println(B_GREEN + "\n[SUCCESS] PAYMENT SUCCESSFUL!" + RESET);
            printReceiptFromServer(in); // print receipt line by line
            paymentJustCompleted = true;
            return true;
        } else if (status.startsWith("SHORT")) {
            //server will tell how much more is needed
            String[] parts = status.split(":");
            if (parts.length > 1) {
                System.out.println(B_RED + "\n[ERROR]: Not enough cash!" + RESET);
                System.out.println(B_YELLOW + "[!] You need an additional PHP " + parts[1] + RESET);
            }
            return false;
        } else {
            System.out.println(B_RED + "\n[ERROR]: " + status + RESET);
            return false;
        }
    }
 
    // Process card payment
    private static boolean processCardAuthAndPayment(Scanner scanner, BufferedReader in, PrintWriter out)
            throws IOException {

        out.println("2"); //tell server card payment is chosen
 
        while (true) {
            displayCardAuthMenu(); // show menu log in, register, back
            System.out.print(" >>> Enter option number: ");
            String authChoice = scanner.nextLine().trim();
 
            switch (authChoice) {
                case "1": // Log in with existing account
                    return processCardLogin(scanner, in, out);

                case "2": // Register a new account
                    boolean registered = processCardRegister(scanner, in, out);
                    if (registered) {
                        // After registration, proceed to login
                        System.out.println(B_GREEN + "\n[SUCCESS] Account registered! Please log in." + RESET);
                        return processCardLogin(scanner, in, out);
                    }
                    // If registration failed, go back to menu
                    break;
 
                case "3": // Back to payment menu
                    System.out.println(B_YELLOW + "[!] Returning to payment options..." + RESET);
                    return false;
 
                default:
                    System.out.println(B_RED + "[ERROR]: Invalid selection. Please enter 1, 2, or 3." + RESET);
            }
        }
    }
 
    // Login with existing account and pay
    private static boolean processCardLogin(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        System.out.println(CYAN + "\n╔══════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║                           " + B_WHITE + "%-47s" + CYAN + "║%n", "CARD PAYMENT - LOG IN");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝" + RESET);
 
        System.out.print("Enter bank account number: ");
        String accountNumber = scanner.nextLine().trim();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine().trim();
 
        out.println("LOGIN");
        out.println(accountNumber);
        out.println(pin);
 
        return handleCardPaymentResponse(in);
    }
 
    // Register a new bank account
    private static boolean processCardRegister(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        System.out.println(CYAN + "\n╔══════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║                           " + B_WHITE + "%-47s" + CYAN + "║%n", "REGISTER NEW ACCOUNT");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  " + B_WHITE + "%-73s" + CYAN + "║%n", "Please fill in your account details below.");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝" + RESET);
 
        System.out.print("Enter account number   : ");
        String newAccNum = scanner.nextLine().trim();
 
        System.out.print("Enter your full name   : ");
        String newName = scanner.nextLine().trim();
 
        System.out.print("Enter initial balance  : PHP ");
        String newBalance = scanner.nextLine().trim();
 
        System.out.print("Create a 4-digit PIN   : ");
        String newPin = scanner.nextLine().trim();
 
        // send server the register commend
        out.println("REGISTER");
        out.println(newAccNum);
        out.println(newName);
        out.println(newBalance);
        out.println(newPin);
 
        
 
        // Wait for server response
        String regStatus = in.readLine();
        if (regStatus == null) {
            System.out.println(B_RED + "[ERROR]: Server disconnected" + RESET);
            return false;
        }
 
        switch (regStatus) {
            case "REGISTER_SUCCESS":
                return true;
            case "REGISTER_DUPLICATE":
                System.out.println(
                        B_RED + "\n[ERROR]: Account number already exists. Please use a different number." + RESET);
                return false;
            case "REGISTER_INVALID":
                System.out.println(
                        B_RED + "\n[ERROR]: Invalid registration data (check balance format or empty fields)." + RESET);
                return false;
            default:
                System.out.println(B_RED + "\n[ERROR]: " + regStatus + RESET);
                return false;
        }
    }
 
    // card payment response handler
    private static boolean handleCardPaymentResponse(BufferedReader in) throws IOException {
        String status = in.readLine();
        if (status == null) {
            System.out.println(B_RED + "[ERROR]: Server disconnected" + RESET);
            return false;
        }
 
        switch (status) {
            case "PAYMENT_SUCCESS":
                System.out.println(B_GREEN + "\n[SUCCESS] CARD PAYMENT SUCCESSFUL!" + RESET);
                printReceiptFromServer(in);
                paymentJustCompleted = true;
                return true;
            case "INVALID_ACCOUNT":
                System.out.println(B_RED + "\n[ERROR]: Invalid account number or PIN." + RESET);
                return false;
            case "INSUFFICIENT_FUNDS":
                System.out.println(B_RED + "\n[ERROR]: Insufficient funds in account." + RESET);
                return false;
            default:
                System.out.println(B_RED + "\n[ERROR]: " + status + RESET);
                return false;
        }
    }
 
    // print receipt, read line until "END_RECEIPT" signal
    private static void printReceiptFromServer(BufferedReader in) throws IOException {
        System.out.println("\n" + B_YELLOW + "═══════════════════════ YOUR RECEIPT ═══════════════════════" + RESET);
        String line;
        while ((line = in.readLine()) != null) {
            if (line.equals("END_RECEIPT")) {
                break; // Server signals end of receipt
            }
            System.out.println(line);
        }
        System.out.println(B_YELLOW + "═══════════════════════════════════════════════════════════" + RESET);
    }
 
    // Display payment method selection menu
    private static void displayPaymentMenu() {
        System.out.println(CYAN + "\n╔══════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║                           " + B_WHITE + "%-47s" + CYAN + "║%n", "SELECT PAYMENT MODE");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                          ║");
        System.out.printf("║                        " + B_WHITE + "%-50s" + CYAN + "║%n", "[1] Cash Payment");
        System.out.printf("║                        " + B_WHITE + "%-50s" + CYAN + "║%n", "[2] Card Payment");
        System.out.printf("║                        " + B_WHITE + "%-50s" + CYAN + "║%n", "[3] Return to Cart");
        System.out.println("║                                                                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝" + RESET);
    }
 
    // Display the card auth (login / register) menu
    private static void displayCardAuthMenu() {
        System.out.println(CYAN + "\n╔══════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║                           " + B_WHITE + "%-47s" + CYAN + "║%n", "CARD PAYMENT - ACCOUNT");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                          ║");
        System.out.printf("║                        " + B_WHITE + "%-50s" + CYAN + "║%n", "[1] Log In  (existing account)");
        System.out.printf("║                        " + B_WHITE + "%-50s" + CYAN + "║%n", "[2] Register  (new account)");
        System.out.printf("║                        " + B_WHITE + "%-50s" + CYAN + "║%n", "[3] Back to Payment Options");
        System.out.println("║                                                                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝" + RESET);
    }
 
    // Display formatted inventory organized by category
    private static void printFormattedInventory(ArrayList<Product> productInventory) {
        // Collect unique category names 
        ArrayList<String> uniqueCategories = new ArrayList<>();
        for (Product p : productInventory) {
            if (!uniqueCategories.contains(p.getProdCategory())) {
                uniqueCategories.add(p.getProdCategory());
            }
        }
        
        // Print one table per category
        for (String cat : uniqueCategories) {
            System.out.println(YELLOW + "╔══════════════════════════════════════════════════════════════════╗");
            System.out.printf("║  " + B_WHITE + "%-64s" + YELLOW + "║%n", cat.toUpperCase());
            System.out.println("╠══════════╦════════════════════════════╦══════════════╦═══════════╣");
            System.out.println("║    ID    ║            NAME            ║     PRICE    ║   STOCK   ║");
            System.out.println("╠══════════╬════════════════════════════╬══════════════╬═══════════╣" + RESET);
 
            for (Product p : productInventory) {
                if (p.getProdCategory().equalsIgnoreCase(cat)) {
                    System.out.printf(
                            YELLOW + "║ " + RESET + "%-8s " + YELLOW + "║ " + RESET + "%-26s " + YELLOW + "║ "
                                    + RESET + "PHP %8.2f " + YELLOW + "║ " + RESET + "Qty: %-4d " + YELLOW + "║%n",
                            p.getProdId(),
                            p.getProdName().substring(0, Math.min(26, p.getProdName().length())),
                            p.getPrice(),
                            p.getStock());
                }
            }
 
            System.out.println(YELLOW + "╚══════════╩════════════════════════════╩══════════════╩═══════════╝" + RESET);
            System.out.println();
        }
    }
}