package com.saga.Sharded.Saga.Wallet.services.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.Sharded.Saga.Wallet.entity.SagaInstance;
import com.saga.Sharded.Saga.Wallet.entity.SagaStep;
import com.saga.Sharded.Saga.Wallet.enums.SagaStatus;
import com.saga.Sharded.Saga.Wallet.enums.StepStatus;
import com.saga.Sharded.Saga.Wallet.repositories.SagaInstanceRepository;
import com.saga.Sharded.Saga.Wallet.repositories.SagaStepRepository;
import com.saga.Sharded.Saga.Wallet.services.saga.steps.SagaStepFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestratorImpl implements SagaOrchestrator{

    private final ObjectMapper objectMapper;

    private final SagaInstanceRepository sagaInstanceRepository;
    private  final SagaStepFactory  sagaStepFactory;
    private  final SagaStepRepository sagaStepRepository;

    @Override
    @Transactional
    public Long startSaga(SagaContext context) {

        try {
            String contextJson  =  objectMapper.writeValueAsString(context); //convert the context to a json as a string
            log.info("context {}" , context);

            SagaInstance sagaInstance =  SagaInstance
                    .builder()
                  .context(contextJson)
                  .status(SagaStatus.STARTED)
                  .build();

            sagaInstance =  sagaInstanceRepository.save(sagaInstance);
            log.info("Started saga with id {}", sagaInstance.getId());

            return sagaInstance.getId();





        }catch (Exception e){
            log.error("Error starting saga", e);
            throw new RuntimeException("Error starting saga", e);
        }


    }

    @Override
    @Transactional
    public boolean executeStep(Long sagaInstanceId, String stepName) {
        SagaInstance sagaInstance  =  sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(()-> new RuntimeException("Saga instance not found"));

        ISagaStep step =  sagaStepFactory.getSagaStep(stepName);

        if(step == null) {
            log.error("Saga step not found for step name {}", stepName);
            throw new RuntimeException("Saga step not found");
        }
//        SagaStep sagaStepDB =  sagaStepRepository.findBySagaInstanceIdAndStatus(sagaInstanceId, StepStatus.PENDING)
//                .stream()
//                .filter(s ->s.getStepName().equals(stepName))
//                .findFirst()
//                .orElse(SagaStep.builder().stepName(stepName).sagaInstanceId(sagaInstanceId).status(StepStatus.PENDING)
//                        .build());

       SagaStep sagaStepDB =  sagaStepRepository.findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId, stepName, StepStatus.PENDING)
               .orElse(SagaStep.builder().stepName(stepName).sagaInstanceId(sagaInstanceId).status(StepStatus.PENDING)
                       .build()
               );

        if(sagaStepDB.getId() == null) {
            sagaStepDB = sagaStepRepository.save(sagaStepDB);
        }

        try {
           SagaContext sagaContext =  objectMapper.readValue(sagaInstance.getContext(), SagaContext.class);
            sagaStepDB.setStatus(StepStatus.RUNNING);
            sagaStepRepository.save(sagaStepDB);

           boolean success =  step.execute(sagaContext);

           if(success) {

               sagaInstance.setCurrentStep(stepName);
               sagaInstance.setStatus(SagaStatus.RUNNING);
               sagaInstanceRepository.save(sagaInstance);

               log.info("step {} executed successfully ", stepName);
               return true;

           }else{

               sagaStepDB.setStatus(StepStatus.FAILED);
               sagaStepRepository.save(sagaStepDB);
               log.info("step {} failed", stepName);
               return false;


           }



        }catch(Exception e){

            sagaStepDB.setStatus(StepStatus.FAILED);
            sagaStepRepository.save(sagaStepDB);


            log.error("failed to execute step", e);


            return false;
        }
    }

    @Override
    @Transactional
    public boolean compensateStep(Long sagaInstanceId, String stepName) {

       SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga Instance not found"));

       ISagaStep step =  sagaStepFactory.getSagaStep(stepName);

        SagaStep sagaStepDB =  sagaStepRepository.findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId, stepName, StepStatus.PENDING)
                .orElse(null);





        if(sagaStepDB.getId() == null) {
            log.info("Step {} not found in the db for saga instance {}, so it is already compensated or not executed", stepName, sagaInstanceId);
            return true;
        }

      try{

         SagaContext context =   objectMapper.readValue(sagaInstance.getContext(),SagaContext.class);
          sagaStepDB.setStatus(StepStatus.COMPENSATING);
          sagaStepRepository.save(sagaStepDB);


          boolean success = step.compensate(context);

          if(success){
              sagaStepDB.setStatus(StepStatus.COMPENSATED);
              sagaStepRepository.save(sagaStepDB);

              log.info("step {} compensated successfully ", stepName);
              return true;
          }else{

              sagaStepDB.setStatus(StepStatus.FAILED);
              sagaStepRepository.save(sagaStepDB);
              log.error("Step {} failed", stepName);
              return false;

          }


      }catch (Exception e){

          sagaStepDB.setStatus(StepStatus.FAILED);
          sagaStepRepository.save(sagaStepDB);
          log.error("Step {} failed", stepName);
          return false;

      }

    }

    @Override
    public SagaInstance getSagaInstance(Long sagaInstanceId) {
       return sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga Instance not found"));


    }

    @Override
    @Transactional
    public void compensateSaga(Long sagaInstanceId) { // if error occurs the saga orchestrator will compensate all completed steps.
        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga Instance not found"));

        List<SagaStep> steps =  sagaStepRepository.findCompletedStepsBySagaInstanceId(sagaInstanceId);

        sagaInstance.setStatus(SagaStatus.COMPENSATING);
        sagaInstanceRepository.save(sagaInstance);

        boolean allCompensated = true;
        for(SagaStep step : steps ){
            boolean compensated = this.compensateStep(sagaInstanceId, step.getStepName());
           if(!compensated){
               allCompensated = false;

           }
        }

        if(allCompensated){
            sagaInstance.setStatus(SagaStatus.COMPENSATED);
            sagaInstanceRepository.save(sagaInstance);
            log.info("saga {} compensated successfully" , sagaInstanceId);

        }else{
            log.error("saga {} compensation failed" , sagaInstanceId);
        }


    }

    @Override
    @Transactional
    public void failSaga(Long sagaInstanceId) {

        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga Instance not found"));
        sagaInstance.setStatus(SagaStatus.FAILED);
        sagaInstanceRepository.save(sagaInstance);

        compensateSaga(sagaInstanceId);

    }

    @Override
    @Transactional
    public void completeSaga(Long sagaInstanceId) {

        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("Saga Instance not found"));
        sagaInstance.setStatus(SagaStatus.COMPLETED);
        sagaInstanceRepository.save(sagaInstance);



    }
}
