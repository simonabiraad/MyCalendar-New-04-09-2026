package com.example.mycalendar2026sar;

public class Account {
    private String name;
    private double balance;
    private String currency;

    public Account(String name, double balance) {
        this(name, balance, "USD");
    }

    public Account(String name, double balance, String currency) {
        this.name = name;
        this.balance = balance;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
