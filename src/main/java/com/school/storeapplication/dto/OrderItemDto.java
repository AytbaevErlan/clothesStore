package com.school.storeapplication.dto;

import java.math.BigDecimal;

public record OrderItemDto(Long productId, String name, int quantity, BigDecimal priceAtPurchase) {}

