package com.fazonplay.farmmyfarm.Models;

public class FinanceManager {
    private double balance;

    public FinanceManager(double initialBalance) {
        this.balance = initialBalance;
    }

    public void addMoney(double amount) {
        balance += amount;
    }

    public boolean canAfford(double amount) {
        return balance >= amount;
    }

    public boolean deductMoney(double amount) {
        if (canAfford(amount)) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public double getBalance() { return balance; }
}