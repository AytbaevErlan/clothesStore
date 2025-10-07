package com.school.storeapplication.dto;

import java.math.BigDecimal;

public record CartItemDto(
        Long productId, String name, int quantity, BigDecimal unitPrice, BigDecimal lineTotal
) {}
