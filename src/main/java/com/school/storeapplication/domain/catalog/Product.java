package com.school.storeapplication.domain.catalog;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)  private String name;
    @Column(nullable = false)  private String description;
    @Column(nullable = false)  private BigDecimal price;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)  private Integer stock;
    private String imageUrl;

    @ManyToOne(optional = false)
    private Category category;

    // NEW: soft-delete flag (default true = visible)
    @Column(nullable = false)
    private boolean active = true;
}
