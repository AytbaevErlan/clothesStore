package com.school.storeapplication.web;

import com.school.storeapplication.service.CatalogService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.school.storeapplication.dto.ProductDto;

@Controller
public class PageController {
    private final CatalogService catalog;
    public PageController(CatalogService c){ this.catalog = c; }

    @GetMapping("/") public String home(){ return "index"; }

    @GetMapping("/products")
    public String products(@RequestParam(name="q", required=false) String q,
                           @RequestParam(name="page", defaultValue="0") int page,
                           @RequestParam(name="size", defaultValue="12") int size,
                           Model model){
        var p = catalog.list(q, page, size);
        model.addAttribute("products", p.getContent());
        model.addAttribute("pg", p);
        model.addAttribute("q", q == null ? "" : q);
        return "products";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // resolves templates/admin.html
    }

    @GetMapping("/login") public String login(){ return "login"; }
    @GetMapping("/register") public String register(){ return "register"; }
    @GetMapping("/cart") public String cart(){ return "cart"; }
    @GetMapping("/orders")
    public String orders(){ return "orders"; }

}
