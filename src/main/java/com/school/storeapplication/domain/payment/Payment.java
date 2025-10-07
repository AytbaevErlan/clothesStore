package com.school.storeapplication.domain.payment;

import com.school.storeapplication.domain.order.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false)
    private Order order;

    private String provider; // MOCK
    private String status; // CREATED, AUTHORIZED, SETTLED
    private Instant createdAt;

}
