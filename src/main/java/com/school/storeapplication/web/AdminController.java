package com.school.storeapplication.web;

import com.school.storeapplication.dto.ProductDto;
import com.school.storeapplication.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto dto) {
        var saved = catalog.createProduct(dto);
        return ResponseEntity
                .created(URI.create("/api/admin/products/" + saved.id()))
                .body(saved);
    }

    @GetMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDto getOne(@PathVariable long id) {
        return catalog.getProduct(id);
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
}
