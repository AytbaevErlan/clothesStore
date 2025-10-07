package com.school.storeapplication.web;

import com.school.storeapplication.dto.ProductDto;
import com.school.storeapplication.service.CatalogService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/catalog")
public class CatalogController {
    private final CatalogService svc;
    public CatalogController(CatalogService svc) { this.svc = svc; }


    @GetMapping("/products")
    public Page<ProductDto> list(@RequestParam(required = false) String q,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "12") int size) {
        return svc.list(q, page, size);
    }


    @GetMapping("/products/{id}")
    public ProductDto get(@PathVariable Long id) { return svc.get(id); }


}
