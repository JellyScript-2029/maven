package com.maven.model;
//ENCAPSULATION
//This class represents a product in the inventory (Products.json)
public class Product {

    // private fields
    private String id; // unique product code
    private String name; // product name
    private String category; // product category
    private double price; //product price
    private int stockQuantity; // product stocks available
    
    //constructor
    public Product(String id, String name, String category, double price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
 
    //Getters
    public String getProdId() {
        return id;
    }
 
    public String getProdName() {
        return name;
    }
 
    public String getProdCategory() {
        return category;
    }
 
    public double getPrice() {
        return price;
    }
 
    public int getStock() {
        return stockQuantity;
    }
 
    // Setters
    public void setStock(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
 
    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", stock=" + stockQuantity +
                '}';
    }
}