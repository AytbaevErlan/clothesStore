package com.school.storeapplication.web;

import com.school.storeapplication.dto.CartDto;
import com.school.storeapplication.service.CartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('BUYER')")
@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService svc;
    public CartController(CartService svc) { this.svc = svc; }


    @GetMapping
    public CartDto view(@AuthenticationPrincipal UserDetails ud) { return svc.view(ud.getUsername()); }


    @PostMapping("/add/{productId}")
    public CartDto add(@AuthenticationPrincipal UserDetails ud, @PathVariable Long productId, @RequestParam(defaultValue = "1") int qty) {
        return svc.add(ud.getUsername(), productId, qty);
    }


    @DeleteMapping("/remove/{productId}")
    public CartDto remove(@AuthenticationPrincipal UserDetails ud, @PathVariable Long productId) {
        return svc.remove(ud.getUsername(), productId);
    }

    @PatchMapping("/set/{productId}")
    public CartDto setQty(@AuthenticationPrincipal UserDetails ud,
                          @PathVariable Long productId,
                          @RequestParam int qty) {
        return svc.setQuantity(ud.getUsername(), productId, qty);
    }
}
