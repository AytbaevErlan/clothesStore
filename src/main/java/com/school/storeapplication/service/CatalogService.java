package com.school.storeapplication.service;

import com.school.storeapplication.domain.catalog.Product;
import com.school.storeapplication.dto.ProductDto;
import com.school.storeapplication.mapper.CatalogMapper;
import com.school.storeapplication.repo.CategoryRepository;
import com.school.storeapplication.repo.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service

public class CatalogService {

    private final ProductRepository products;
    private final CategoryRepository categories;
    private final CatalogMapper mapper;

    public CatalogService(ProductRepository products,
                          CategoryRepository categories,
                          CatalogMapper mapper) {
        this.products = products;
        this.categories = categories;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return products.findAllByActiveTrue().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(long id) {
        var p = products.findById(id).orElseThrow();
        return mapper.toDto(p);
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        // ProductDto is a record -> use accessors
        if (products.existsBySku(dto.sku())) {
            throw new IllegalArgumentException("SKU already exists");
        }

        var category = categories.findById(dto.categoryId()).orElseThrow();

        Product entity = Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .sku(dto.sku())
                .stock(dto.stock())
                .imageUrl(dto.imageUrl())
                .category(category)
                .build();

        return mapper.toDto(products.save(entity));
    }

    @Transactional
    public void deleteProduct(long id) {
        var p = products.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        p.setActive(false);                // SOFT DELETE
        products.save(p);
    }


    @Transactional(readOnly = true)
    public Page<ProductDto> list(String q, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("id").descending());
        var pageEntities = (q == null || q.isBlank())
                ? products.findByActiveTrue(pageable)
                : products.findByActiveTrueAndNameContainingIgnoreCase(q, pageable);
        return pageEntities.map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProductDto get(Long id) {
        var p = products.findById(id).orElseThrow();
        if (!p.isActive()) throw new IllegalStateException("Product inactive");
        return mapper.toDto(p);
    }



}
