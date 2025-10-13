package com.saga.Sharded.Saga.Wallet.entity;

import com.saga.Sharded.Saga.Wallet.enums.SagaStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "saga_instance")
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SagaStatus status = SagaStatus.STARTED;

    @Column(name = "context", columnDefinition = "json")
    private String context; //key value pair

    @Column(name = "current_step")
    private String currentStep;




}
