package com.school.storeapplication.mapper;

import com.school.storeapplication.domain.catalog.Category;
import com.school.storeapplication.domain.catalog.Product;
import com.school.storeapplication.dto.CategoryDto;
import com.school.storeapplication.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CatalogMapper {
    CategoryDto toDto(Category c);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerEmail", source = "seller.email")
    ProductDto toDto(Product p);
}
