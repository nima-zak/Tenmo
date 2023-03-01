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
