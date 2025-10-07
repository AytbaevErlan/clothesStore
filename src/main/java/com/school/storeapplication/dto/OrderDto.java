package com.school.storeapplication.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDto(Long id, BigDecimal total, Instant createdAt, List<OrderItemDto> items) {}
