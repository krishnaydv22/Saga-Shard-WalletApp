package com.saga.Sharded.Saga.Wallet.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditWalletResponseDTO {

    private Long id;
    private Long userId;
    private BigDecimal balance;
    private boolean isActive;


}
