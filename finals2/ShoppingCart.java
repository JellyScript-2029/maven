package com.maven.model;

import java.util.HashMap;
import java.util.Map;
// GENERIC CLASS

public class ShoppingCart<T> {
    // hashmap where key is the product id and value is the quantity
    // use map to quickly look up any product
    private Map<String, Integer> cartItems;
    
    // running total of the bill in PHP
    private double totalPrice;

    //constructor
    public ShoppingCart() {
        this.cartItems = new HashMap<>(); 
        this.totalPrice = 0.0;
    }

    //Adds a product to the cart 
    public void addItem(String productId, int quantity, double priceEach) {
        // if product already in cart, just increase quantity
        if (cartItems.containsKey(productId)) {
            int currentQty = cartItems.get(productId);
            cartItems.put(productId, currentQty + quantity);
        } else {
            // add new product 
            cartItems.put(productId, quantity);
        }

        //update the running total
        this.totalPrice += (priceEach * quantity);
    }

    // GETTERS
    public Map<String, Integer> getCartItems() {
        return cartItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    //Empties the cart after checkout or cancellation.
    public void clearCart() {
        cartItems.clear();
        totalPrice = 0.0;
    }
}
