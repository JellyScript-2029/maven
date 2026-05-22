package com.maven.model;
// INHERITANCE
//PARENT CLASS
public class Person {
    protected String name;
    protected String accountNumber;

    public Person(String accountNumber, String name) {
        this.accountNumber = accountNumber;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setId(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "accountNumber='"   + accountNumber   + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}