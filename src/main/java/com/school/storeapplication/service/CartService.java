package com.school.storeapplication.service;

import com.school.storeapplication.domain.cart.Cart;
import com.school.storeapplication.domain.cart.CartItem;
import com.school.storeapplication.domain.catalog.Product;
import com.school.storeapplication.domain.user.User;
import com.school.storeapplication.dto.*;
import com.school.storeapplication.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class CartService {
    private final CartRepository carts;
    private final ProductRepository products;
    private final UserRepository users;

    public CartService(CartRepository carts, ProductRepository products, UserRepository users) {
        this.carts = carts; this.products = products; this.users = users;
    }


    private Cart cartOf(String email) {
        User u = users.findByEmail(email).orElseThrow();
        var cart = carts.findByUser(u).orElseGet(() ->
                carts.save(Cart.builder().user(u).items(new ArrayList<>()).build())
        );
        if (cart.getItems() == null) cart.setItems(new ArrayList<>());
        return cart;
    }

    @Transactional(readOnly = true)
    public CartDto view(String email) {
        var cart = cartOf(email);
        var total = BigDecimal.ZERO;
        var items = cart.getItems().stream()
                .map(ci -> {
                    var price = ci.getProduct().getPrice();
                    var line = price.multiply(BigDecimal.valueOf(ci.getQuantity()));
                    return new CartItemDto(ci.getProduct().getId(), ci.getProduct().getName(),
                            ci.getQuantity(), price, line);
                }).toList();

        for (var i : items) total = total.add(i.lineTotal());
        return new CartDto(items, total);
    }

    @Transactional
    public CartDto add(String email, Long productId, int qty) {
        var cart = cartOf(email);
        Product p = products.findByIdAndActiveTrue(productId).orElseThrow();
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be at least 1");
        if (p.getStock() < qty) throw new IllegalArgumentException("Not enough stock for " + p.getName());

        var existing = cart.getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).findFirst();
        if (existing.isPresent()) {
            int newQty = existing.get().getQuantity() + qty;
            if (p.getStock() < newQty) throw new IllegalArgumentException("Not enough stock for " + p.getName());
            existing.get().setQuantity(newQty);
        } else {
            cart.getItems().add(CartItem.builder().cart(cart).product(p).quantity(qty).build());
        }
        return view(email);
    }

    @Transactional
    public CartDto remove(String email, Long productId) {
        var cart = cartOf(email);
        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        return view(email);
    }

    @Transactional
    public void clear(String email) {
        var cart = cartOf(email);
        cart.getItems().clear();
    }

    @Transactional
    public CartDto setQuantity(String email, Long productId, int qty) {
        if (qty < 0) qty = 0;
        var cart = cartOf(email);
        var item = cart.getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).findFirst().orElse(null);
        if (item == null && qty == 0) return view(email);
        var p = products.findByIdAndActiveTrue(productId).orElseThrow();
        if (qty > p.getStock()) throw new IllegalArgumentException("Not enough stock for " + p.getName());
        if (qty == 0) cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        else { if (item == null) cart.getItems().add(CartItem.builder().cart(cart).product(p).quantity(qty).build()); else item.setQuantity(qty); }
        return view(email);
    }


}
