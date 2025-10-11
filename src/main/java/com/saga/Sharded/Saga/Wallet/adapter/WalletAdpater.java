package com.saga.Sharded.Saga.Wallet.adapter;

import com.saga.Sharded.Saga.Wallet.dtos.CreditWalletResponseDTO;
import com.saga.Sharded.Saga.Wallet.entity.Wallet;

public class WalletAdpater {

    public static CreditWalletResponseDTO toDto(Wallet wallet){
        return CreditWalletResponseDTO.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .isActive(wallet.isActive())
                .build();

    }
}
