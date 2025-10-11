package com.saga.Sharded.Saga.Wallet.controllers;

import com.saga.Sharded.Saga.Wallet.adapter.WalletAdpater;
import com.saga.Sharded.Saga.Wallet.dtos.CreateWalletRequestDTO;
import com.saga.Sharded.Saga.Wallet.dtos.CreditWalletRequestDTO;
import com.saga.Sharded.Saga.Wallet.dtos.CreditWalletResponseDTO;
import com.saga.Sharded.Saga.Wallet.dtos.DebitWalletRequestDTO;
import com.saga.Sharded.Saga.Wallet.entity.Wallet;
import com.saga.Sharded.Saga.Wallet.services.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequiredArgsConstructor
@RequestMapping("/wallets")
@Slf4j
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody CreateWalletRequestDTO request) {
        try {
            Wallet newWallet = walletService.createWallet(request.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(newWallet);
        } catch (Exception e) {
            log.error("Error creating wallet", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWalletById(@PathVariable Long id) {
        Wallet wallet = walletService.getWalletById(id);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getWalletBalance(@PathVariable Long id) {
        BigDecimal balance = walletService.getWalletBalance(id);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/{userId}/debit")
    public ResponseEntity<Wallet> debitWallet(@PathVariable Long userId, @RequestBody DebitWalletRequestDTO request) {
        Wallet wallet =  walletService.debit(userId, request.getAmount());

        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/{userId}/credit")
    public ResponseEntity<CreditWalletResponseDTO> creditWallet(@PathVariable Long userId, @RequestBody CreditWalletRequestDTO request) {
        Wallet wallet = walletService.credit(userId, request.getAmount());

        return ResponseEntity.ok(WalletAdpater.toDto(wallet));
    }
}
