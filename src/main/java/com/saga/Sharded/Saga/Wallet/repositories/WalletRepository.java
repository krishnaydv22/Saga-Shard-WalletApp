package com.saga.Sharded.Saga.Wallet.repositories;

import com.saga.Sharded.Saga.Wallet.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {

    Wallet findByUserId(Long userId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet WHERE w.id = :id")
    Optional<Wallet> findByIdWithLock(@Param("id") Long id);
}
