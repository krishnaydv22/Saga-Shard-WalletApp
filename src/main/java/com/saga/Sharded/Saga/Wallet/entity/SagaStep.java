package com.saga.Sharded.Saga.Wallet.entity;

import com.saga.Sharded.Saga.Wallet.enums.SagaStatus;
import com.saga.Sharded.Saga.Wallet.enums.StepStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "saga_step")
public class SagaStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saga_instance_id", nullable = false)
    private Long sagaInstanceId;

    @Column(name = "step_name", nullable = false)
    private String stepName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StepStatus status = StepStatus.STARTED;

    @Column(name ="error_message", nullable = true)
    private String errorMessage;

    @Column(name = "step_data", columnDefinition = "json")
    private String stepData;






}
