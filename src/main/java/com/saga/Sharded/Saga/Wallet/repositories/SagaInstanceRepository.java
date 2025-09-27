package com.saga.Sharded.Saga.Wallet.repositories;

import com.saga.Sharded.Saga.Wallet.entity.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {

}
