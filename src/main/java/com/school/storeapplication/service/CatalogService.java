package com.school.storeapplication.service;

import com.school.storeapplication.domain.catalog.Product;
import com.school.storeapplication.dto.ProductDto;
import com.school.storeapplication.mapper.CatalogMapper;
import com.school.storeapplication.repo.CategoryRepository;
import com.school.storeapplication.repo.ProductRepository;
import com.school.storeapplication.repo.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogService {

    private final ProductRepository products;
    private final CategoryRepository categories;
    private final CatalogMapper mapper;
    private final UserRepository users;

    public CatalogService(ProductRepository products,
                          CategoryRepository categories,
                          CatalogMapper mapper,
                          UserRepository users) {
        this.products = products;
        this.categories = categories;
        this.mapper = mapper;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> list(String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> ents = (q == null || q.isBlank())
                ? products.findAllByActiveTrue(pageable)
                : products.findByNameContainingIgnoreCaseAndActiveTrue(q, pageable);
        return ents.map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return products.findAllByActiveTrue(Pageable.unpaged())
                .getContent().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ProductDto get(long id) {
        var p = products.findByIdAndActiveTrue(id).orElseThrow();
        return mapper.toDto(p);
    }

    /** Admin create: optional sellerId allowed */
    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        if (products.existsBySku(dto.sku())) throw new IllegalArgumentException("SKU already exists");
        var category = categories.findById(dto.categoryId()).orElseThrow();
        var seller = (dto.sellerId() != null) ? users.findById(dto.sellerId()).orElse(null) : null;

        var entity = Product.builder()
                .name(dto.name()).description(dto.description())
                .price(dto.price()).sku(dto.sku()).stock(dto.stock())
                .imageUrl(dto.imageUrl()).category(category)
                .seller(seller)
                .active(true)
                .build();
        return mapper.toDto(products.save(entity));
    }

    /** Seller create: owner is the authenticated seller email */
    @Transactional
    public ProductDto createProductForSeller(String sellerEmail, ProductDto dto) {
        if (products.existsBySku(dto.sku())) throw new IllegalArgumentException("SKU already exists");
        var category = categories.findById(dto.categoryId()).orElseThrow();
        var seller = users.findByEmail(sellerEmail).orElseThrow();

        var entity = Product.builder()
                .name(dto.name()).description(dto.description())
                .price(dto.price()).sku(dto.sku()).stock(dto.stock())
                .imageUrl(dto.imageUrl()).category(category)
                .seller(seller)
                .active(true)
                .build();
        return mapper.toDto(products.save(entity));
    }

    @Transactional
    public ProductDto updateProduct(long id, ProductDto dto) {
        var entity = products.findById(id).orElseThrow();
        if (!entity.getSku().equals(dto.sku()) && products.existsBySku(dto.sku()))
            throw new IllegalArgumentException("SKU already exists");

        var cat = categories.findById(dto.categoryId()).orElseThrow();
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setPrice(dto.price());
        entity.setSku(dto.sku());
        entity.setStock(dto.stock());
        entity.setImageUrl(dto.imageUrl());
        entity.setCategory(cat);
        if (dto.active() != null) entity.setActive(dto.active());
        if (dto.sellerId() != null) {
            entity.setSeller(users.findById(dto.sellerId()).orElse(null));
        }
        return mapper.toDto(products.save(entity));
    }

    @Transactional
    public void deleteProduct(long id) {
        var p = products.findById(id).orElseThrow();
        p.setActive(false);
        products.save(p);
    }

    @Transactional
    public void setActive(long id, boolean active) {
        var p = products.findById(id).orElseThrow();
        p.setActive(active);
        products.save(p);
    }

    @Transactional
    public ProductDto updateProductForSeller(String sellerEmail, long id, ProductDto dto) {
        var entity = products.findById(id).orElseThrow();
        if (entity.getSeller() == null || !sellerEmail.equals(entity.getSeller().getEmail()))
            throw new IllegalArgumentException("Forbidden: not owner");

        if (!entity.getSku().equals(dto.sku()) && products.existsBySku(dto.sku()))
            throw new IllegalArgumentException("SKU already exists");

        var cat = categories.findById(dto.categoryId()).orElseThrow();
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setPrice(dto.price());
        entity.setSku(dto.sku());
        entity.setStock(dto.stock());
        entity.setImageUrl(dto.imageUrl());
        entity.setCategory(cat);
        return mapper.toDto(products.save(entity));
    }

    @Transactional
    public void deleteProductForSeller(String sellerEmail, long id) {
        var p = products.findById(id).orElseThrow();
        if (p.getSeller() == null || !sellerEmail.equals(p.getSeller().getEmail()))
            throw new IllegalArgumentException("Forbidden: not owner");
        p.setActive(false);
        products.save(p);
    }

}
