package com.saga.Sharded.Saga.Wallet.services;

import com.saga.Sharded.Saga.Wallet.entity.SagaInstance;
import com.saga.Sharded.Saga.Wallet.entity.Transaction;
import com.saga.Sharded.Saga.Wallet.services.TransactionService;
import com.saga.Sharded.Saga.Wallet.services.saga.SagaContext;
import com.saga.Sharded.Saga.Wallet.services.saga.SagaOrchestrator;
import com.saga.Sharded.Saga.Wallet.services.saga.steps.SagaStepFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferSagaService {

    private final TransactionService transactionService;
    private final SagaOrchestrator sagaOrchestrator;

    @Transactional
    public long initiateTransfer(Long fromWalletId,
                                 Long toWalletId,
                                 BigDecimal amount,
                                 String description) {

        log.info("Initiating transfer from wallet {} to wallet {} with amount {} and description {}", fromWalletId, toWalletId, amount, description);

        Transaction transaction = transactionService.createTransaction(fromWalletId, toWalletId, amount, description);

        SagaContext sagaContext = SagaContext.builder()
                .data(Map.ofEntries(
                        Map.entry("transactionId", transaction.getId()),
                        Map.entry("fromWalletId", fromWalletId), //fromWalletId and toWalletId should be the  userId
                        Map.entry("toWalletId", toWalletId),
                        Map.entry("amount", amount),
                        Map.entry("description", description)
                ))
                .build();

        Long sagaInstanceId = sagaOrchestrator.startSaga(sagaContext); // creating the saga

        log.info("saga instance created with id {}", sagaInstanceId);

        transactionService.updateTransactionWithSagaInstanceId(transaction.getId(), sagaInstanceId);

        executeTransferSaga(sagaInstanceId);

        return sagaInstanceId;


    }

    @Transactional
    public void executeTransferSaga(Long sagaInstanceId) {


        log.info("Executing transfer saga with id {}", sagaInstanceId);
        try {
            for (SagaStepFactory.SagaStepType step : SagaStepFactory.TransferMoneySagaSteps) { //order of execution
                boolean success = sagaOrchestrator.executeStep(sagaInstanceId, step.toString());
                if (!success) {
                    log.info("failed to execute step {}", step.toString());
                    sagaOrchestrator.failSaga(sagaInstanceId);  // mark as failed and compensate all completed steps
                    return;
                }
            }

            sagaOrchestrator.completeSaga(sagaInstanceId);
            log.info("transfer saga completed with id  {}", sagaInstanceId);


        } catch (Exception e) {
            sagaOrchestrator.completeSaga(sagaInstanceId);
            log.info("transfer saga completed with id  {}", sagaInstanceId);


        }

    }

}






