package com.saga.Sharded.Saga.Wallet.enums;

public enum StepStatus {
    STARTED,
    RUNNING,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED,
    SKIPPED
}
