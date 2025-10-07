package com.school.storeapplication.dto;


import java.math.BigDecimal;

public record ProductDto(
        Long id, String name, String description, BigDecimal price,
        String sku, Integer stock, String imageUrl, Long categoryId, String categoryName
) {}