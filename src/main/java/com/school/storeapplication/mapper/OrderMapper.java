package com.school.storeapplication.mapper;

import com.school.storeapplication.domain.order.OrderItem;
import com.school.storeapplication.dto.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "name",      source = "product.name")
    OrderItemDto toDto(OrderItem item);
}
