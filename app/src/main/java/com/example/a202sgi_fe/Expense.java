
package com.example.a202sgi_fe;
public class Expense {
    private String id;  // Firebase generates string keys
    private double amount;
    private String category;
    private String date;
    private String description;

    // Required empty constructor for Firebase
    public Expense() {}

    public Expense(String id, double amount, String category, String date, String description) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
}
