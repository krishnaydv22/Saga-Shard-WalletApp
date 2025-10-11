package com.saga.Sharded.Saga.Wallet.repositories;

import com.saga.Sharded.Saga.Wallet.entity.Wallet;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {

    List<Wallet> findByUserId(Long userId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.userId = :id")
    Optional<Wallet> findByIdWithLock(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Wallet w SET w.balance = :balance WHERE w.userId = :userId")
    int updateBalanceByUserId(@Param("userId") Long userId, @Param("balance") BigDecimal balance);
}
