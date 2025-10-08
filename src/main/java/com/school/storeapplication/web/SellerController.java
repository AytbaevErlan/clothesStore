package com.school.storeapplication.web;

import com.school.storeapplication.dto.ProductDto;
import com.school.storeapplication.service.CatalogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('SELLER')")
public class SellerController {

    private final CatalogService catalog;

    public SellerController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @PostMapping("/products")
    public ProductDto create(@AuthenticationPrincipal UserDetails ud,
                             @RequestBody @Validated ProductDto dto) {
        return catalog.createProductForSeller(ud.getUsername(), dto);
    }

    @PutMapping("/products/{id}")
    public ProductDto update(@AuthenticationPrincipal UserDetails ud,
                             @PathVariable long id,
                             @RequestBody @Validated ProductDto dto) {
        return catalog.updateProductForSeller(ud.getUsername(), id, dto);
    }

    @DeleteMapping("/products/{id}")
    public void delete(@AuthenticationPrincipal UserDetails ud,
                       @PathVariable long id) {
        catalog.deleteProductForSeller(ud.getUsername(), id);
    }
}
