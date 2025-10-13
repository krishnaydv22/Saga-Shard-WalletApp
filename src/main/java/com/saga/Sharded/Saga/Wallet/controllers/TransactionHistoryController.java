package com.saga.Sharded.Saga.Wallet.controllers;

import com.saga.Sharded.Saga.Wallet.entity.TransactionHistory;
import com.saga.Sharded.Saga.Wallet.services.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @GetMapping
    public ResponseEntity<List<TransactionHistory>> getUserTransactionHistory(long userId){

        List<TransactionHistory> txHistory =  transactionHistoryService.getTransactionHistory(userId);

        return ResponseEntity.ok(txHistory);

    }
}
