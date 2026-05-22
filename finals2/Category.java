package com.maven.model;
 
//ENCAPSULATION
public class Category {
    private String categoryName; // the name of the category
    
    //CONSTRUCTOR
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
 
    //GETTER
    public String getCategoryName() {
        return categoryName;
    }
 
    //SETTER
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "categoryName='" + categoryName + '\'' +
                '}';
    }   
    
   //two category objects are considered the same if their category name is the same
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
 
        Category category = (Category) o;
        return categoryName != null ? categoryName.equals(category.categoryName) : category.categoryName == null;
    }
    
    // overriden together wih equals() so that is works correctly with category keys
    @Override
    public int hashCode() {
        return categoryName != null ? categoryName.hashCode() : 0;
    }
}
 