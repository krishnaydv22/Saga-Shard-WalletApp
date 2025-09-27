package com.saga.Sharded.Saga.Wallet.enums;

public enum SagaStatus {
    STARTED,
    RUNNING,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED
}
