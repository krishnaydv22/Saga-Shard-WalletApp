package com.saga.Sharded.Saga.Wallet.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "user_id", nullable = false)
    private Long userId;

    @Column(name= "is_active", nullable = false)
    private boolean isActive;

    @Column(name= "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;


    public boolean hasSufficientBalance(BigDecimal amount){
        return balance.compareTo(amount) >= 0;
    }


    public void debit(BigDecimal amount){

        if(!hasSufficientBalance(amount)){
            throw new IllegalArgumentException("insufficient balance");

        }
        balance = balance.subtract(amount);

    }

    public void credit(BigDecimal amount){

        balance = balance.add(amount);


    }
}
