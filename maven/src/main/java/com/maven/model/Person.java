package com.maven.model;
// INHERITANCE
//PARENT CLASS
public class Person {
    protected String name;
    protected String id;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='"   + id   + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}