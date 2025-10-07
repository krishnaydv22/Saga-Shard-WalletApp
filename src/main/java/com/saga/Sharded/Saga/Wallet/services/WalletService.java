package com.saga.Sharded.Saga.Wallet.services;

import java.math.BigDecimal;
import java.util.List;

import com.saga.Sharded.Saga.Wallet.entity.Wallet;
import com.saga.Sharded.Saga.Wallet.repositories.WalletRepository;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public Wallet createWallet(Long userId) {
        log.info("Creating wallet for user {}", userId);
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .isActive(true)
                .balance(BigDecimal.ZERO)
                .build();
        wallet = walletRepository.save(wallet);
        log.info("Wallet created with id {}", wallet.getId());
        return wallet;
    }

    public Wallet getWalletById(Long id) {
        log.info("Getting wallet by id {}", id);
        return walletRepository.findById(id).orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public List<Wallet> getWalletsByUserId(Long userId) {
        return walletRepository.findByUserId(userId);
    }

    @Transactional
    public void debit(Long walletId, BigDecimal amount) {
        log.info("Debiting {} to wallet {}", amount, walletId);
        Wallet wallet = getWalletById(walletId);

        wallet.debit(amount);
        walletRepository.save(wallet);
        log.info("debit {} successfully for wallet {}",amount, walletId);



    }

    public Wallet getWalletByUserId(Long userId) {
        log.info("Getting wallet by user id {}", userId);
        return walletRepository.findByUserId(userId).get(0);
    }

    @Transactional
    public void credit(Long walletId, BigDecimal amount) {
        log.info("Crediting {} to wallet {}", amount, walletId);
        Wallet wallet = getWalletById(walletId);

        wallet.credit(amount);
        walletRepository.save(wallet);
        log.info("credited {} successfully for wallet {}",amount, walletId);

    }

    public BigDecimal getWalletBalance(Long walletId) {
        log.info("Getting balance for wallet {}", walletId);
        BigDecimal balance = getWalletById(walletId).getBalance();
        log.info("Balance for wallet {} is {}", walletId, balance);
        return balance;
    }





}
