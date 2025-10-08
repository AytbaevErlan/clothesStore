package com.school.storeapplication.web;

import com.school.storeapplication.dto.ProductDto;
import com.school.storeapplication.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


    private final CatalogService catalog;

    public AdminController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductDto> getAllProducts() {
        return catalog.getAllProducts();
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(@RequestBody @Validated ProductDto dto) {
        try {
            var saved = catalog.createProduct(dto);
            return ResponseEntity.status(201).body(saved);
        } catch (IllegalArgumentException iae) {
            // custom app-level validation errors
            return ResponseEntity.status(409).body(Map.of("error", iae.getMessage()));
        } catch (Exception e) {
            // log to console for visibility
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of(
                    "error", e.getClass().getSimpleName(),
                    "message", e.getMessage()
            ));
        }
    }


    @GetMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDto getOne(@PathVariable long id) {
        return catalog.get(id);
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable long id) {
        try {
            catalog.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            return ResponseEntity.status(409)
                    .body("Cannot delete product; it is referenced by existing orders or carts.");
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            return ResponseEntity.notFound().build();
        }
    }

//    @PutMapping("/products/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ProductDto update(@PathVariable long id, @RequestBody ProductDto dto) {
//        return catalog.updateProduct(id, dto);
//    }


    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> update(
            @PathVariable long id, @Validated @RequestBody ProductDto dto) {
        return ResponseEntity.ok(catalog.updateProduct(id, dto));
    }

    @PatchMapping("/products/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setActive(
            @PathVariable long id,
            @RequestParam boolean active) {
        catalog.setActive(id, active);
        return ResponseEntity.noContent().build();
    }



}
