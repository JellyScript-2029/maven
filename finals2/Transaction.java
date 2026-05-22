package com.maven.model;
 //ENCAPSULATION
//This class represents a completed transaction
public class Transaction {

    //private fields
    private int transactionNumber; // increment when there is a new tramsaction
    private String dateOfPurchase; // time stamp as a String
    private double price; // total amount paid
    
    //costructor
    public Transaction(int transactionNumber, String dateOfPurchase, double price) {
        this.transactionNumber = transactionNumber;
        this.dateOfPurchase = dateOfPurchase;
        this.price = price;
    }
 
    // Getters
    public int getTransactionNumber() {
        return transactionNumber;
    }
 
    public String getDateOfPurchase() {
        return dateOfPurchase;
    }
 
    public double getPrice() {
        return price;
    }
 
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionNumber=" + transactionNumber +
                ", dateOfPurchase='" + dateOfPurchase + '\'' +
                ", price=" + price +
                '}';
    }
}
 