package com.school.storeapplication.domain.cart;

import com.school.storeapplication.domain.catalog.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Cart cart;
    @ManyToOne(optional = false)
    private Product product;
    private int quantity;
}
