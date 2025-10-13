package com.saga.Sharded.Saga.Wallet.services;

import com.saga.Sharded.Saga.Wallet.entity.TransactionHistory;
import com.saga.Sharded.Saga.Wallet.repositories.UserTransactionHistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final UserTransactionHistoryRepo userTransactionHistoryRepo;

    public List<TransactionHistory> getTransactionHistory(long userId){
        return userTransactionHistoryRepo.findByUserId(userId);

    }

    public TransactionHistory saveTransactionHistory(TransactionHistory history){
        return userTransactionHistoryRepo.save(history);
    }



}
