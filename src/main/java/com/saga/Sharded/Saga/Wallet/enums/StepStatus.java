package com.saga.Sharded.Saga.Wallet.enums;

public enum StepStatus {
    STARTED,

    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED,
    SKIPPED
}
