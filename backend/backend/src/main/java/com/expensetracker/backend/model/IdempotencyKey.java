package com.expensetracker.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
@Entity
@Table(name = "idempotency_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyKey {

    @Id
    @Column(name = "idempotency_key")   // ✅ FIXED (no 'key')
    private String key;

    @Column(columnDefinition = "TEXT")
    private String response;

    private Instant createdAt;
}