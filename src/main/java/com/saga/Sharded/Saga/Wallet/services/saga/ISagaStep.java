package com.saga.Sharded.Saga.Wallet.services.saga;

public interface ISagaStep {

    boolean execute(SagaContext context);

    boolean compensate(SagaContext context);

    String getName();
}
