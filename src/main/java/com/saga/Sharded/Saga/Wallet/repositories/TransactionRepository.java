package com.saga.Sharded.Saga.Wallet.repositories;

import com.saga.Sharded.Saga.Wallet.entity.Transaction;
import com.saga.Sharded.Saga.Wallet.entity.Wallet;
import com.saga.Sharded.Saga.Wallet.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TransactionRepository  extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromWalletId(Long fromWalletId); //all debit transactions
    List<Transaction> findByToWalletId(Long toWalletId); //all credit transactions


    @Query("SELECT t FROM Transaction t where t.fromWalletId = :walletId  OR t.toWalletId = :walletId" )
    List<Transaction> findByWalletId(@Param("walletId") Long walletId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findBySagaInstanceId(Long sagaInstanceId);

}
