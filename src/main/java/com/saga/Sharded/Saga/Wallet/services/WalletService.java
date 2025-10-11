package com.saga.Sharded.Saga.Wallet.services;

import java.math.BigDecimal;
import java.util.List;

import com.saga.Sharded.Saga.Wallet.entity.Wallet;
import com.saga.Sharded.Saga.Wallet.repositories.WalletRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService {

    @PersistenceContext
    private EntityManager entityManager;


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









    public Wallet getWalletByUserId(Long userId) {
        log.info("Getting wallet by user id {}", userId);
        return walletRepository.findByUserId(userId).get(0);
    }

    @Transactional
    public Wallet debit(Long userId, BigDecimal amount) {
        log.info("Debiting {} to userId {}", amount, userId);
        Wallet wallet = getWalletByUserId(userId);
//        wallet.setBalance(wallet.getBalance().subtract(amount)); // will not work, because sharding is done based on userId

        walletRepository.updateBalanceByUserId(userId,wallet.getBalance().subtract(amount));//we are updating based on userId, because sharding of wallet has been done based on userId
        // Clear persistence context to avoid stale data
        entityManager.flush();
        entityManager.clear();

        Wallet updatedWallet = getWalletByUserId(userId);

        log.info("debit {} successfully for wallet {}",amount, userId);

        return updatedWallet;



    }

    @Transactional
    public Wallet credit(Long userId, BigDecimal amount) {
        log.info("Crediting {} to userId {}", amount, userId);
        Wallet wallet = getWalletByUserId(userId);

        // wallet.setBalance(wallet.getBalance().subtract(amount)); // will not work, because sharding is done based on userId



        walletRepository.updateBalanceByUserId(userId,wallet.getBalance().add(amount)); //we are updating based on userId, because sharding of wallet has been done based on userId
        //Note ->  update and delete return int value not Entity

        // Clear persistence context to avoid stale data

        entityManager.flush();
        entityManager.clear();

        Wallet updatedWallet = getWalletByUserId(userId);

        log.info("credited {} successfully for wallet {}",amount, userId);

        return updatedWallet;

    }

    public BigDecimal getWalletBalance(Long walletId) {
        log.info("Getting balance for wallet {}", walletId);
        BigDecimal balance = getWalletById(walletId).getBalance();
        log.info("Balance for wallet {} is {}", walletId, balance);
        return balance;
    }





}
