package com.saga.Sharded.Saga.Wallet.repositories;

import com.saga.Sharded.Saga.Wallet.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTransactionHistoryRepo  extends JpaRepository<TransactionHistory,Long> {

    List<TransactionHistory> findByUserId(Long id);
}
