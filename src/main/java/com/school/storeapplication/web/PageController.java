package com.school.storeapplication.web;

import com.school.storeapplication.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final CatalogService catalog;

    public PageController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/products")
    public String productsPage(@RequestParam(required = false) String q,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "12") int size,
                               Model model) {
        var pg = catalog.list(q, page, size);
        model.addAttribute("products", pg.getContent());
        model.addAttribute("pg", pg);
        model.addAttribute("q", q == null ? "" : q);
        return "products";
    }

    @GetMapping("/cart")
    public String cartPage() { return "cart"; }

    @GetMapping("/orders")
    public String ordersPage() { return "orders"; }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @GetMapping("/admin")
    public String adminPage() { return "admin"; }
}
