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
public class CreditDestinationWalletStep  implements ISagaStep {

    private final WalletRepository walletRepository;


    @Override
    @Transactional
    public boolean execute(SagaContext context) {

        //1 . get the destination wallet id from context
        Long toWalletId =  context.getLong("toWalletId"); // id of destination user
         BigDecimal amount =  context.getBigDecimal("amount");

         log.info("Crediting destination wallet {} with amount {}" , toWalletId, amount);




        //2. fetch the destination wallet from db
        Wallet wallet =  walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow( () -> new IllegalArgumentException("wallet not found"));

        log.info("wallet fetched with balance {} " , wallet.getBalance());

        context.put("originalToWalletBalance", wallet.getBalance());

        //3. credit destination wallet

       walletRepository.updateBalanceByUserId(toWalletId, wallet.getBalance().add(amount));
        log.info("Wallet saved with balance {}", wallet.getBalance());

        //4. update the context with the changes

        context.put("toWalletBalanceAfterCredit", wallet.getBalance());

        log.info("Credit destination wallet step executed successfully");

        return  true;

    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context) {

       Long toWalletId =  context.getLong("toWalletId");
       BigDecimal amount = context.getBigDecimal("amount");

       log.info("compensation credit of destination wallet {} with  amount {} ", toWalletId,amount );

        Wallet wallet =  walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new IllegalArgumentException("wallet not found "));
        log.info("wallet fetched with balance {}" , wallet.getBalance());

        walletRepository.updateBalanceByUserId(toWalletId, wallet.getBalance().subtract(amount));

        log.info("Wallet compensated with current balance {}", wallet.getBalance());
        context.put("toWalletBalanceAfterCreditCompensation", wallet.getBalance());

        log.info("Credit compensation of destination wallet step executed successfully");
        return true;


    }

    @Override
    public String getName() {
        return SagaStepFactory.SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString();
    }
}
