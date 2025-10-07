package com.saga.Sharded.Saga.Wallet.services.saga.steps;

import com.saga.Sharded.Saga.Wallet.entity.Wallet;
import com.saga.Sharded.Saga.Wallet.repositories.WalletRepository;
import com.saga.Sharded.Saga.Wallet.services.saga.ISagaStep;
import com.saga.Sharded.Saga.Wallet.services.saga.SagaContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebitSourceWalletStep implements ISagaStep {

    private final WalletRepository walletRepository;


    @Override
    @Transactional
    public boolean execute(SagaContext context) {

        //1. get the source id from context
       Long fromWalletId =   context.getLong("fromWalletId");
       BigDecimal amount =  context.getBigDecimal("amount");

       log.info("debiting source wallet {} with amount {}", fromWalletId, amount);

       //2. get the wallet fo source user
        Wallet wallet =  walletRepository.findByIdWithLock(fromWalletId)
                .orElseThrow( () -> new IllegalArgumentException("wallet not found"));

        log.info("Wallet fetched with balance {}", wallet.getBalance());
        context.put("originalSourceWalletBalance", wallet.getBalance());

        //3. debit the amount from the user
        wallet.debit(amount);

        walletRepository.save(wallet);
        log.info("Wallet saved with balance {}", wallet.getBalance());

        //4. update the context

        context.put("sourceWalletBalanceAfterDebit", wallet.getBalance());

        log.info("Debit source wallet step executed successfully");

        return true;
    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context) {


        //1. get the source id from context
        Long fromWalletId =   context.getLong("fromWalletId");
        BigDecimal amount =  context.getBigDecimal("amount");

        log.info("compensating source wallet {} with amount {}", fromWalletId, amount);

        //2. get the wallet fo source user
        Wallet wallet =  walletRepository.findByIdWithLock(fromWalletId)
                .orElseThrow( () -> new IllegalArgumentException("wallet not found"));

        log.info("Wallet fetched with balance {}", wallet.getBalance());
        context.put("sourceWalletBalanceBeforeCreditCompensation", wallet.getBalance());

        //3. debit the amount from the user
        wallet.credit(amount);

        walletRepository.save(wallet);
        log.info("Wallet saved with balance {}", wallet.getBalance());

        //4. update the context

        context.put("sourceWalletBalanceAfterCreditCompensation", wallet.getBalance());

        log.info("compensating source wallet step executed successfully");

        return true;

    }

    @Override
    public String getName() {
        return SagaStepFactory.SagaStepType.DEBIT_SOURCE_WALLET_STEP.toString();
    }
}
