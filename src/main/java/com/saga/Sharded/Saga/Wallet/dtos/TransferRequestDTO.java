package com.saga.Sharded.Saga.Wallet.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequestDTO {

    private Long fromWalletId; // fromUserId
    private Long toWalletId; // toUserId
    private BigDecimal amount;
    private String description;
}
