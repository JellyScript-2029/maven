package com.maven.service;
 
import com.google.gson.reflect.TypeToken;
import com.maven.model.*;
 
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
 
 
// Manages client connections, inventory, and financial data
public class Server {
 
    // CONFIGURATION
    public static final int PORT = 8080;
    public static final String PRODUCTS_FILE = "Products.json";
    public static final String BANK_FILE = "BankAccounts.json";
    public static final String TRANSACTION_FILE = "Transaction.json";
    public static final String RECEIPT_FILE = "receipt.txt";
    
    // THREAD-SAFE SHARED DATA
    // Use synchronized collections to prevent data corruption from multiple clients
    public static List<Product> products = new ArrayList<>();
    public static List<BankAccount> bankAccounts = new ArrayList<>();
    public static List<Transaction> transactions = new ArrayList<>();
 
    // FINANCIAL TRACKING
    private static final Object financeLock = new Object(); // Lock for financial operations
    public static double cashDrawerBalance = 2000.0; // base balance for cash drawer
    public static double shopBankBalance = 1000.0; // base balance for bank balance
 
    // CLIENT MANAGEMENT
    private static int clientCounter = 0;
    private static final Object clientCounterLock = new Object(); // Lock for client counter
 
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     JJ STORE SERVER - INITIALIZING     ║");
        System.out.println("╚════════════════════════════════════════╝");
 
        loadServerData(); // read json files into memory
        startOwnerMenuThread(); // background thread for admin
        startServerSocket(); // listen for client connections
    }
 
    // Load all server data from JSON files
    private static void loadServerData() {
        System.out.println("\n[LOADING DATA...]");
 
        // Load products
        products = FileManager.loadListFromJson(PRODUCTS_FILE,
                new TypeToken<ArrayList<Product>>() {
                }.getType());
        if (products.isEmpty()) {
            System.out.println("[ERROR] Products.json not found or empty!");
        } else {
            System.out.println("[SUCCESS] Loaded " + products.size() + " products");
        }
 
        // Load bank accounts
        bankAccounts = FileManager.loadListFromJson(BANK_FILE,
                new TypeToken<ArrayList<BankAccount>>() {
                }.getType());
        if (bankAccounts.isEmpty()) {
            System.out.println("[ERROR] BankAccounts.json not found or empty!");
        } else {
            System.out.println("[SUCCESS] Loaded " + bankAccounts.size() + " bank accounts");
        }
 
        // Load transactions
        transactions = FileManager.loadListFromJson(TRANSACTION_FILE,
                new TypeToken<ArrayList<Transaction>>() {
                }.getType());
        System.out.println("[SUCCESS] Loaded " + transactions.size() + " transactions");
 
        System.out.println("[SUCCESS] All data loaded successfully!\n");
    }
 
    //OWNER MENU THREAD (MULTITHREADING)
    // Start the owner menu in a separate thread
    private static void startOwnerMenuThread() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Type '120616' to access the Hidden Owner Menu.");
            while (true) {
                String input = scanner.nextLine();
                if (input.equals("120616")) {
                    openOwnerMenu(scanner);
                }
            }
        }, "OwnerMenuThread").start();
    }
    //MAIN SERVER SOCKET (MULTITHREADING)
    // Start the main server socket and accept client connections
    private static void startServerSocket() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("═══════════════════════════════════════════");
            System.out.println(" Server is ONLINE on PORT " + PORT);
            System.out.println(" Waiting for customer connections...");
            System.out.println("═══════════════════════════════════════════\n");
 
            while (true) {
                Socket clientSocket = serverSocket.accept(); //wait for connection
                String clientLabel = generateClientLabel(); // example of client label: Client1
                System.out.println("[SUCCESS] " + clientLabel + " connected");
 
                // Create a new thread for each client connection
                ClientHandler handler = new ClientHandler(clientSocket, clientLabel);
 
                // Wrap it in a Thread so it runs independently
                Thread clientThread = new Thread(handler, clientLabel);
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Server exception: " + e.getMessage());
        }
    }
 
    // CLIENT LABEL 
    private static String generateClientLabel() {
        synchronized (clientCounterLock) {
            return "Client" + (++clientCounter);
        }
    }
 
    // OWNER ADMIN PANEL
    private static void openOwnerMenu(Scanner scanner) {
        while (true) {
           System.out.println("\n╔═════════════════════════════════════════════════════════╗");
            System.out.println("║            [OWNER ADMIN PANEL] - SECRET ACCESS          ║");
            System.out.println("╠═════════════════════════════════════════════════════════╣");
            System.out.println("║  [1] View Vault & Shop Bank Balances                    ║");
            System.out.println("║  [2] View All Transactions                              ║");
            System.out.println("║  [3] View Full Product Inventory & Stock                ║");
            System.out.println("║  [4] Exit Admin Panel                                   ║");
            System.out.println("╚═════════════════════════════════════════════════════════╝");
            System.out.print(" >>> Select option: ");
 
            String choice = scanner.nextLine();
 
            switch (choice) {
                case "1" -> displayFinancialReport();
                case "2" -> displayTransactionReport();
                case "3" -> displayInventoryReport();
                case "4" -> {
                    System.out.println("[SUCCESS] Exiting admin panel...\n");
                    return;
                }
                default -> System.out.println("[ERROR] Invalid selection!");
            }
        }
    }
    
    // [1] View Vault & Shop Bank Balances
    // Display financial balances (thread-safe)
    private static void displayFinancialReport() {
        synchronized (financeLock) {
           System.out.println("\n╔═════════════════════════════════════════════════════════╗");
            System.out.println("║                    FINANCIAL REPORT                     ║");
            System.out.println("╠═════════════════════════════════════════════════════════╣");
            System.out.printf("║  Cash Drawer Balance:         PHP %20.2f  ║%n", cashDrawerBalance);
            System.out.printf("║  Shop Bank Account Balance:   PHP %20.2f  ║%n", shopBankBalance);
            System.out.printf("║  TOTAL ASSETS:                PHP %20.2f  ║%n",
                    (cashDrawerBalance + shopBankBalance));
            System.out.println("╚═════════════════════════════════════════════════════════╝");
        }
    }
    
    // [2] View All Transactions 
    // Display all transactions 
    private static void displayTransactionReport() {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                TRANSACTION HISTORY REPORT                ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
 
        if (transactions.isEmpty()) {
            System.out.println("║  No transactions recorded yet.                           ║");
        } else {
            System.out.println("║  TXN#  │  Date  │  Amount                                ║");
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            for (Transaction tx : transactions) {
                System.out.printf("║  %-4d │ %s │ PHP %12.2f                    ║%n",
                        tx.getTransactionNumber(),
                        tx.getDateOfPurchase().substring(0, Math.min(10, tx.getDateOfPurchase().length())),
                        tx.getPrice());
            }
        }
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
    
    // [3] View Full Product Inventory & Stock 
    // Display complete inventory with stock levels
    private static void displayInventoryReport() {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║             COMPLETE INVENTORY & STOCK REPORT            ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
 
        Map<String, List<Product>> categorized = new HashMap<>();
        for (Product p : products) {
            categorized.computeIfAbsent(p.getProdCategory(), k -> new ArrayList<>()).add(p);
        }
 
        for (String category : categorized.keySet()) {
            System.out.println("║  " + category.toUpperCase());
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            for (Product p : categorized.get(category)) {
                String stockStatus = p.getStock() == 0 ? " OUT OF STOCK!" : "Qty: " + p.getStock();
                System.out.printf("║  %-6s │ %-28s │ %-15s ║%n",
                        p.getProdId(), p.getProdName().substring(0, Math.min(28, p.getProdName().length())),
                        stockStatus);
            }
        }
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
 
    // reserve stock when an item is added to cart (SYNCHRONIZED)
    // Immediately deducts stock so other clients see updated quantity in real time
    public static synchronized boolean reserveStock(String productId, int quantity) {
        Product product = findProductById(productId);
 
        if (product == null || product.getStock() < quantity) {
            System.out.println("[WARNING] Reserve failed - insufficient stock for: " + productId);
            return false; // if out of stock
        }
 
        // Deduct stock immediately so live stock reflects the reservation
        product.setStock(product.getStock() - quantity);
        FileManager.saveListToJson(PRODUCTS_FILE, products);
        System.out.println("[SUCCESS] Reserved " + quantity + "x " + productId +
                " | Remaining stock: " + product.getStock());
        return true; // if stock was available and reserved
    }
 
    // restore the product stock when a cart is cancelled or client disconnects (SYNCHRONIZED)
    public static synchronized void restoreStock(String productId, int quantity) {
        Product product = findProductById(productId);
 
        if (product != null) {
            product.setStock(product.getStock() + quantity);
            FileManager.saveListToJson(PRODUCTS_FILE, products);
            System.out.println("[SUCCESS] Restored " + quantity + "x " + productId +
                    " | Updated stock: " + product.getStock());
        }
    }
 
    // stock is already reserved when items were added to other clients cart
    public static synchronized boolean processCartCheckout(ShoppingCart<Product> cart) {
 
        // Stock was already deducted during reserveStock() when items were added to cart    
        FileManager.saveListToJson(PRODUCTS_FILE, products);
        return true;
    }
 
    //find a product by ID (THREAD-SAFE)
    public static Product findProductById(String prodId) {
        for (Product p : products) {
            if (p.getProdId().equalsIgnoreCase(prodId)) {
                return p;
            }
        }
        return null;
    }
 
    //find a bank account by number and PIN (THREAD-SAFE)
    public static BankAccount findBankAccount(String accountNumber, String pin) {
        for (BankAccount acc : bankAccounts) {
            if (acc.getIdNumber().equals(accountNumber) && acc.getPin().equals(pin)) {
                return acc;
            }
        }
        return null;
    }
 
    // find a bank account by number only  (THREAD-SAFE)
    // used when register an account
    public static BankAccount findBankAccountByNumber(String accountNumber) {
        for (BankAccount acc : bankAccounts) {
            if (acc.getIdNumber().equals(accountNumber)) {
                return acc;
            }
        }
        return null;
    }
 
    // Safely deduct from bank account (SYNCHRONIZED)
    // total bill will be safely deducted to the clients bank
    public static synchronized boolean deductFromBankAccount(BankAccount account, double amount) {
        if (account.getBalance() < amount) {
            return false;
        }
        account.setBalance(account.getBalance() - amount);
        FileManager.saveListToJson(BANK_FILE, bankAccounts);
        return true;
    }
 
    // Safely add sales to cash drawer balance (SYNCHRONIZED)
    public static synchronized void addCashDrawerBalance(double amount) {
        cashDrawerBalance += amount;
    }
 
    // Safely add sales to shop bank balance (SYNCHRONIZED)
    public static synchronized void addShopBankBalance(double amount) {
        shopBankBalance += amount;
    }
}