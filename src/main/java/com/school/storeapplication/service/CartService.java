package com.school.storeapplication.service;

import com.school.storeapplication.domain.cart.Cart;
import com.school.storeapplication.domain.cart.CartItem;
import com.school.storeapplication.domain.catalog.Product;
import com.school.storeapplication.domain.user.User;
import com.school.storeapplication.dto.*;
import com.school.storeapplication.repo.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartService {
    private final CartRepository carts; private final ProductRepository products; private final UserRepository users;
    public CartService(CartRepository carts, ProductRepository products, UserRepository users) { this.carts = carts; this.products = products; this.users = users; }


    private Cart cartOf(String email) {
        User u = users.findByEmail(email).orElseThrow();
        return carts.findByUser(u).orElseGet(() -> carts.save(Cart.builder().user(u).build()));
    }


    public CartDto view(String email) {
        var cart = cartOf(email);
        var total = BigDecimal.ZERO;
        var items = cart.getItems().stream().map(ci -> {
            var price = ci.getProduct().getPrice();
            var line = price.multiply(BigDecimal.valueOf(ci.getQuantity()));
            return new CartItemDto(ci.getProduct().getId(), ci.getProduct().getName(), ci.getQuantity(), price, line);
        }).toList();
        for (var i : items) total = total.add(i.lineTotal());
        return new CartDto(items, total);
    }


    public CartDto add(String email, Long productId, int qty) {
        var cart = cartOf(email);
        Product p = products.findById(productId).orElseThrow();
        var existing = cart.getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).findFirst();
        if (existing.isPresent()) existing.get().setQuantity(existing.get().getQuantity() + qty);
        else cart.getItems().add(CartItem.builder().cart(cart).product(p).quantity(qty).build());
        carts.save(cart);
        return view(email);
    }


    public CartDto remove(String email, Long productId) {
        var cart = cartOf(email);
        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        carts.save(cart);
        return view(email);
    }


    public void clear(String email) {
        var cart = cartOf(email);
        cart.getItems().clear();
        carts.save(cart);
    }
}
