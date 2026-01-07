package com.jpmc.midascore.entity;

public class Balance {
    private double balance;

    public Balance()
    {
        //default constructor
    }
    public Balance(double balance)
    {
        this.balance=balance;
    }

    public double getBalance()
    {
        return balance;

    }

    public void setBalance(double balance)
    {
        this.balance=balance;
    }
    // balance field stores the user’s current balance.

    // Spring will automatically serialize this to JSON.
}
