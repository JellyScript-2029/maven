package com.maven.service;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Map;
 
import com.google.gson.Gson;
import com.maven.model.BankAccount;
import com.maven.model.FileManager;
import com.maven.model.Product;
import com.maven.model.ShoppingCart;
import com.maven.model.Transaction;
 
//This class handles individual client connections
public class ClientHandler implements Runnable { // RUNNABLE so that it can be run inside a thread
    private final Socket socket;
    private final String clientLabel;
    private final Gson gson = new Gson();
 
    // CONSTRUCTOR
    public ClientHandler(Socket socket, String clientLabel) {
        this.socket = socket;
        this.clientLabel = clientLabel;
    }
 
    @Override
    public void run() { // entry point of the clients thread
 
        // each client get their own private shopping cart
        ShoppingCart<Product> clientCart = new ShoppingCart<>();
 
        // BufferedReader reads text lines sent by the client
        // PrintWriter sends text lines to the client
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
 
            // Serialize the shared product list to JSON and send it to the client.
            while (true) {
 
                // Since stock is reserved immediately, this always reflects real-time
                // quantities
                String serializedProducts = gson.toJson(Server.products);
                out.println(serializedProducts);
 
                // wait for the clients next action
                String primaryAction = in.readLine();
 
                // if client disconnected unexpectedly
                if (primaryAction == null) {
                    break;
                }
 
                if (primaryAction.equalsIgnoreCase("CANCEL")) {
                    // Customer chose to exit the store
                    System.out.println("[SUCCESS] " + clientLabel + " disconnected");
                    break;
                } else if (primaryAction.equalsIgnoreCase("BUY")) {
                    // Client wants to buy then process purchase
                    processPurchase(in, out, clientCart);
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] " + clientLabel + " connection error: " + e.getMessage());
        } finally {
            // If client disconnects unexpectedly with items still in cart,
            if (!clientCart.getCartItems().isEmpty()) {
                System.out.println("[WARNING] " + clientLabel + " disconnected with items in cart. Restoring stock...");
                // restore all reserved stock back to the inventory so others can buy it.
                for (Map.Entry<String, Integer> entry : clientCart.getCartItems().entrySet()) {
                    Server.restoreStock(entry.getKey(), entry.getValue());
                }
                clientCart.clearCart(); // clean the cart
            }
 
            // close the socket connection
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("[SUCCESS] " + clientLabel + " session closed");
        }
    }
 
    // Process a complete purchase transaction
    private void processPurchase(BufferedReader in, PrintWriter out, ShoppingCart<Product> clientCart)
            throws IOException {
 
        // Receive the product ID and quantity from the client
        String productId = in.readLine();
        String qtyStr = in.readLine();
 
        // if wrong input
        if (!validateInput(productId, qtyStr)) {
            out.println("FAILED: Invalid input");
            return;
        }
 
        // if wrong input
        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                out.println("FAILED: Quantity must be positive");
                return;
            }
        } catch (NumberFormatException e) {
            out.println("FAILED: Invalid quantity format");
            return;
        }
 
        // Find product first to get price
        Product selectedProduct = Server.findProductById(productId);
 
        // if product is not on the list
        if (selectedProduct == null) {
            out.println("FAILED: Product not found");
            return;
        }
 
        // Server.reserveStock() deducts stock immediately, this always reflects real-time quantities
        // Other clients will only see the reduced quantity on their next refresh
        boolean reserved = Server.reserveStock(productId, quantity);
        if (!reserved) {
            out.println("FAILED: Insufficient stock");
            return;
        }
 
        // Add to this client's private cart
        clientCart.addItem(productId, quantity, selectedProduct.getPrice());
        out.println("SUCCESS");
 
        // Move to the checkout menu loop
        checkoutMenu(in, out, clientCart);
    }
 
    // allows customer to add more products, proceed to payment, or cancel
    private void checkoutMenu(BufferedReader in, PrintWriter out, ShoppingCart<Product> clientCart)
            throws IOException {
        boolean checkoutActive = true;
 
        while (checkoutActive) {
            // Send current total price
            double currentTotal = clientCart.getTotalPrice();
            out.println(String.valueOf(currentTotal));
 
            // Get customer's choice
            String choice = in.readLine();
            if (choice == null) {
                break;
            }
 
            switch (choice) {
                case "1" -> // Customer wants to add another product
                    addAnotherProduct(in, out, clientCart);
                case "2" -> {  // Customer wants to pay
                    boolean paymentSuccess = processPaymentFlow(in, out, clientCart);
                    if (paymentSuccess) {
                        checkoutActive = false; // Return to main menu after payment
                    }
                }
                case "3" -> {  // Customer wants to cancel the whole transaction
                    String confirm = in.readLine();
                    if (confirm != null && (confirm.equalsIgnoreCase("YES") || confirm.equalsIgnoreCase("yes"))) {
                        // Restore all reserved stock back to inventory
                        for (Map.Entry<String, Integer> entry : clientCart.getCartItems().entrySet()) {
                            Server.restoreStock(entry.getKey(), entry.getValue());
                        }
                        clientCart.clearCart();
                        checkoutActive = false;
                    }
                }
                default -> out.println("ERROR: Invalid choice");
            }
        }
    }
 
    // Add another product to the cart
    private void addAnotherProduct(BufferedReader in, PrintWriter out, ShoppingCart<Product> clientCart)
            throws IOException {
        String nextId = in.readLine();
        String nextQtyStr = in.readLine();
 
        if (!validateInput(nextId, nextQtyStr)) {
            out.println("FAILED");
            return;
        }
 
        try {
            int nextQty = Integer.parseInt(nextQtyStr);
            if (nextQty <= 0) {
                out.println("FAILED");
                return;
            }
 
            // Find product to get price
            Product nextProduct = Server.findProductById(nextId);
            if (nextProduct == null) {
                out.println("FAILED");
                return;
            }
 
            // Reserve stock for the new item (synchronized in Server)
            boolean reserved = Server.reserveStock(nextId, nextQty);
            if (!reserved) {
                out.println("FAILED");
            } else {
                clientCart.addItem(nextId, nextQty, nextProduct.getPrice());
                out.println("SUCCESS");
            }
        } catch (NumberFormatException e) {
            out.println("FAILED");
        }
    }
 
    // choose between cash and card
    private boolean processPaymentFlow(BufferedReader in, PrintWriter out, ShoppingCart<Product> clientCart)
            throws IOException {
        String paymentMethod = in.readLine();
        if (paymentMethod == null) {
            return false;
        }
 
        double currentTotal = clientCart.getTotalPrice();
 
        switch (paymentMethod) {
            case "1" -> { // cash payment
                return processCashPayment(in, out, clientCart, currentTotal);
            }
 
            case "2" -> { // card payment
                return processCardFlow(in, out, clientCart, currentTotal);
            }
 
            case "3" -> { // Cancel payment, return to cart (stock remains reserved)
                return false;
            }
 
            default -> {
                out.println("ERROR: Invalid payment method");
                return false;
            }
        }
    }
 
    // Process CASH payment
    private boolean processCashPayment(BufferedReader in, PrintWriter out, ShoppingCart<Product> clientCart,
            double totalAmount) throws IOException {
        
        // Receive the amount of cash the customer handed over
        String cashStr = in.readLine();
 
        try {
            double cashRendered = Double.parseDouble(cashStr);
            // Customer didn't give enough — tell them the shortfall
            if (cashRendered < totalAmount) {
                double shortAmount = totalAmount - cashRendered;
                out.println("SHORT:" + shortAmount);
                return false;
            }
 
            // Stock already deducted
            boolean checkoutSuccess = Server.processCartCheckout(clientCart);
            if (!checkoutSuccess) {
                out.println("PAYMENT_FAILED");
                return false;
            }
 
            // Add payment to cash drawer
            Server.addCashDrawerBalance(totalAmount);
 
            // Calculate change and build receipt
            double change = cashRendered - totalAmount;
            String receipt = generateReceipt(clientCart, totalAmount, "CASH", cashRendered, change);
 
            // Send success and receipt to client
            out.println("PAYMENT_SUCCESS");
            sendReceiptToClient(out, receipt);
 
            // Clear cart for next purchase
            clientCart.clearCart();
            return true;
 
        } catch (NumberFormatException e) {
            out.println("PAYMENT_FAILED");
            return false;
        }
    }
 
    // card payment handles register and log in
    private boolean processCardFlow(BufferedReader in, PrintWriter out, ShoppingCart<Product> clientCart,
            double totalAmount) throws IOException {
        
        // REGISTER OR LOG IN
        String subCommand = in.readLine();
        if (subCommand == null) {
            out.println("PAYMENT_FAILED");
            return false;
        }
 
        // new account registration
        if (subCommand.equalsIgnoreCase("REGISTER")) {
            String accountNumber = in.readLine();
            String name = in.readLine();
            String balanceStr = in.readLine();
            String pin = in.readLine();
 
            // Basic validation — no blank fields
            if (accountNumber == null || name == null || balanceStr == null || pin == null
                    || accountNumber.isBlank() || name.isBlank() || pin.isBlank()) {
                out.println("REGISTER_INVALID");
                return false;
            }
 
            double initialBalance;
            try {
                initialBalance = Double.parseDouble(balanceStr);
                if (initialBalance < 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                out.println("REGISTER_INVALID");
                return false;
            }
 
            // Reject duplicate account numbers
            if (Server.findBankAccountByNumber(accountNumber) != null) {
                out.println("REGISTER_DUPLICATE");
                return false;
            }
 
            // Create and save the new account
            BankAccount newAccount = new BankAccount(accountNumber, name, initialBalance, pin);
            Server.bankAccounts.add(newAccount);
            FileManager.saveListToJson(Server.BANK_FILE, Server.bankAccounts);
            System.out.println("[SUCCESS] New bank account registered: " + accountNumber + " (" + name + ")");
 
            out.println("REGISTER_SUCCESS");
 
            // After registering, the client sends "LOGIN" next
            subCommand = in.readLine();
            if (subCommand == null) {
                out.println("PAYMENT_FAILED");
                return false;
            }
        }
 
        //log in after registering a new account
        if (subCommand.equalsIgnoreCase("LOGIN")) {
            return processCardPayment(in, out, clientCart, totalAmount);
        }
 
        out.println("ERROR: Unknown card sub-command");
        return false;
    }
 
    // verify account and deduct balance
    private boolean processCardPayment(BufferedReader in, PrintWriter out, ShoppingCart<Product> clientCart,
            double totalAmount) throws IOException {
        String accountNumber = in.readLine();
        String pin = in.readLine();
 
        if (accountNumber == null || pin == null) {
            out.println("PAYMENT_FAILED");
            return false;
        }
 
        // find account where number and pin both match
        BankAccount account = Server.findBankAccount(accountNumber, pin);
        if (account == null) {
            out.println("INVALID_ACCOUNT");
            return false;
        }
 
        // Check sufficient funds
        if (account.getBalance() < totalAmount) {
            out.println("INSUFFICIENT_FUNDS");
            return false;
        }
 
        // stock already reserved
        boolean checkoutSuccess = Server.processCartCheckout(clientCart);
        if (!checkoutSuccess) {
            out.println("PAYMENT_FAILED");
            return false;
        }
 
        // Deduct from customer's account balance
        boolean deducted = Server.deductFromBankAccount(account, totalAmount);
        if (!deducted) {
            out.println("PAYMENT_FAILED");
            return false;
        }
 
        // Add to shop's bank account
        Server.addShopBankBalance(totalAmount);
 
        // Generate receipt
        String receipt = generateReceipt(clientCart, totalAmount, "CARD", totalAmount, 0);
 
        // Send success and receipt
        out.println("PAYMENT_SUCCESS");
        sendReceiptToClient(out, receipt);
 
        // Clear cart for next purchase
        clientCart.clearCart();
        return true;
    }
 
    // Generate a formatted receipt
    private String generateReceipt(ShoppingCart<Product> clientCart, double billTotal, String paymentMode,
            double amountPaid, double change) {
        // BUG 3 FIX: synchronize on transactions list to prevent duplicate IDs
        // when two clients check out at the same time
        int txId;
        String timestamp = new Date().toString();
        Transaction tx;
        synchronized (Server.transactions) {
            txId = Server.transactions.size() + 1;
            tx = new Transaction(txId, timestamp, billTotal);
            Server.transactions.add(tx);
        }
 
        // Save transaction to file
        FileManager.saveListToJson(Server.TRANSACTION_FILE, Server.transactions);
 
        // Calculate tax (price is VAT-inclusive at 12%)
        double subtotal = billTotal / 1.12;
        double tax = billTotal - subtotal;
 
        // Build receipt
        StringBuilder receipt = new StringBuilder();
        receipt.append("------------------------------------------\n");
        receipt.append("              J J   S T O R E            \n");
        receipt.append("------------------------------------------\n");
        receipt.append(String.format(" TXN: #%-32d  \n", txId));
        receipt.append(String.format(" Date: %-32s  \n", timestamp.substring(0, Math.min(25, timestamp.length()))));
        receipt.append("------------------------------------------\n");
        receipt.append(" ITEMS PURCHASED:                       \n");
 
        // List all items in cart
        for (Map.Entry<String, Integer> entry : clientCart.getCartItems().entrySet()) {
            String prodId = entry.getKey();
            int quantity = entry.getValue();
 
            Product product = Server.findProductById(prodId);
            if (product != null) {
                String name = product.getProdName().length() > 28 ? product.getProdName().substring(0, 25) + "..."
                        : product.getProdName();
                receipt.append(String.format("  -> %-34s  \n", name));
                receipt.append(String.format("    Qty: %-3d  |  Unit: PHP %-10.2f   \n", quantity, product.getPrice()));
            }
        }
 
        receipt.append("------------------------------------------\n");
        receipt.append(String.format(" Subtotal: PHP %-24.2f \n", subtotal));
        receipt.append(String.format(" Tax (12%% VAT): PHP %-12.2f \n", tax));
        receipt.append(String.format(" TOTAL: PHP %-27.2f \n", billTotal));
        receipt.append("------------------------------------------\n");
        receipt.append(String.format(" Payment Method: %-22s \n", paymentMode));
 
        if ("CASH".equals(paymentMode)) {
            receipt.append(String.format(" Cash Given: PHP %-22.2f \n", amountPaid));
            receipt.append(String.format(" Change: PHP %-26.2f \n", change));
        } else if ("CARD".equals(paymentMode)) {
            receipt.append(" Card Payment: Successfully Processed   \n");
        }
 
        receipt.append("------------------------------------------\n");
        receipt.append("    Thank you for shopping at JJ Store!   \n");
        receipt.append("------------------------------------------\n");
 
        String receiptText = receipt.toString();
 
        // Save to receipt.txt (synchronized inside FileManager)
        FileManager.appendReceipt(receiptText);
 
        return receiptText;
    }
 
    // Send receipt to client line by line with END_RECEIPT signal
    private void sendReceiptToClient(PrintWriter out, String receipt) {
        String[] lines = receipt.split("\\r?\\n", -1);
        for (String line : lines) {
            out.println(line);
        }
        out.println("END_RECEIPT"); // receipt is complete
    }
 
    // Validate product ID and quantity input
    private boolean validateInput(String productId, String quantity) {
        return productId != null && !productId.trim().isEmpty() &&
                quantity != null && !quantity.trim().isEmpty();
    }
}