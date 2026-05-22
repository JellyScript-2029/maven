package com.maven.model;

//ENCAPSULATION and INHERITANCE
//This class represents a customer's bank account
public class BankAccount extends Person {

    //private fields 
    private double balance; // current money in the account (PHP)
    private String pin; // 4 digit security pin
    
    //constructor
    // this is called when a new Bankaccount object is created
    public BankAccount(String accountNumber, String name, double balance, String pin) {
        super(accountNumber, name);  // Let Person handle id and name
        this.balance = balance;
        this.pin     = pin;
    }
    // GETTER
    public String getIdNumber() {
        return accountNumber;  
    }
    
    public double getBalance() {
        return balance;
    }

    public String getPin() {
        return pin;
    }
 
    //setters
    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }
 
    //used for debugging, prints all fields in a readable format
    @Override
    public String toString() {
        return "BankAccount{" +
                "accountNumber='" + accountNumber + '\'' +
                ", name='"        + name + '\'' +  
                ", balance="      + balance +
                ", pin='"         + pin + '\'' +
                '}';
    }
}