package com.saga.Sharded.Saga.Wallet.config;

import com.saga.Sharded.Saga.Wallet.services.saga.ISagaStep;
import com.saga.Sharded.Saga.Wallet.services.saga.steps.CreditDestinationWalletStep;
import com.saga.Sharded.Saga.Wallet.services.saga.steps.DebitSourceWalletStep;
import com.saga.Sharded.Saga.Wallet.services.saga.steps.SagaStepFactory;
import com.saga.Sharded.Saga.Wallet.services.saga.steps.UpdateTransactionStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SagaConfiguration {

    @Bean
    public Map<String, ISagaStep> sagaStepMap(DebitSourceWalletStep debitSourceWalletStep, CreditDestinationWalletStep creditDestinationWalletStep,
                                              UpdateTransactionStatus updateTransactionStatus){

        Map<String, ISagaStep> map = new HashMap<>();

        map.put(SagaStepFactory.SagaStepType.DEBIT_SOURCE_WALLET_STEP.toString(), debitSourceWalletStep);
        map.put(SagaStepFactory.SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString(), creditDestinationWalletStep);
        map.put(SagaStepFactory.SagaStepType.UPDATE_TRANSACTION_STATUS_STEP.toString(), updateTransactionStatus);

        return map;





    }


}
