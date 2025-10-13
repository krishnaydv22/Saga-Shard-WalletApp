package com.saga.Sharded.Saga.Wallet.services.saga.steps;

import com.saga.Sharded.Saga.Wallet.entity.Transaction;
import com.saga.Sharded.Saga.Wallet.enums.TransactionStatus;
import com.saga.Sharded.Saga.Wallet.repositories.TransactionRepository;
import com.saga.Sharded.Saga.Wallet.services.saga.ISagaStep;
import com.saga.Sharded.Saga.Wallet.services.saga.SagaContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateTransactionStatus implements ISagaStep {

    private final TransactionRepository transactionRepository;


    @Override
    @Transactional
    public boolean execute(SagaContext context) {

        Long transactionId =  context.getLong("transactionId");

        log.info("updating the transaction status of {}", transactionId);

       Transaction transaction =  transactionRepository.findById(transactionId)
               .orElseThrow(() -> new IllegalArgumentException("transaction not found"));

       context.put("originalTransactionStatus",transaction.getStatus());

       transaction.setStatus(TransactionStatus.SUCCESS);

       transactionRepository.save(transaction);

       log.info("Transaction status updated for transaction {}" , transactionId);

       context.put("transactionStatusAfterUpdate", transaction.getStatus());
       log.info("update transaction step executed successfully");


       return true;
    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context) {
        Long transactionId = context.getLong("transactionId");

        TransactionStatus originalTransactionStatus = TransactionStatus.valueOf(context.getString("originalTransactionStatus"));

        log.info("Compensating transaction status for transaction {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setStatus(originalTransactionStatus);
        transactionRepository.save(transaction);

        log.info("Transaction status compensated for transaction {}", transactionId);

        return true;
    }

    @Override
    public String getName() {
        return SagaStepFactory.SagaStepType.UPDATE_TRANSACTION_STATUS_STEP.toString();
    }
}
