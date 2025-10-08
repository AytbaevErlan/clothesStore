package com.school.storeapplication.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductDto(
        Long id,
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        @NotBlank String sku,
        @NotNull @Min(0) Integer stock,
        @Size(max = 2048) String imageUrl,
        @NotNull Long categoryId,
        String categoryName,
        Boolean active,
        Long sellerId,
        String sellerEmail
) {}
