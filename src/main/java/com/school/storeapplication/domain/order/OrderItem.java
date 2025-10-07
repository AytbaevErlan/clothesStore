package com.school.storeapplication.domain.order;

import com.school.storeapplication.domain.catalog.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;
    private BigDecimal priceAtPurchase;

}