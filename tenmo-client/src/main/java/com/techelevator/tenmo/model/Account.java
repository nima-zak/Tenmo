package com.techelevator.tenmo.model;

import lombok.*;

import java.math.BigDecimal;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account {
    private int accountId;
    private int userId;
    private BigDecimal balance;
}






/*import java.math.BigDecimal;

public class Account {
    private int user_id;
    private long account_id;
    private BigDecimal balance;

    public Account(){ }

    public Account(int user_id, long account_id, BigDecimal balance) {
        this.user_id = user_id;
        this.account_id = account_id;
        this.balance = balance;
    }

    public long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Account{" +
                "user_id=" + user_id +
                ", account_id=" + account_id +
                ", balance=" + balance +
                '}';
    }
}*/
