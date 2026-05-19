package com.maven.model;
//ENUM

public enum Payment {
    CASH("Cash Payment"), // customer pays with physical money
    CARD("Card Payment"); // customer pays via bank card
    
    //each constant carries a human readable label
    private final String displayName;
    
    //private constructors
    Payment(String displayName) {
        this.displayName = displayName;
    }
    
    //getter
    public String getDisplayName() {
        return displayName;
    }
    
    
    @Override
    public String toString() {
        return displayName;
    }
}
 