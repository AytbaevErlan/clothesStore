package com.school.storeapplication.service;

import com.school.storeapplication.domain.catalog.Product;
import com.school.storeapplication.domain.order.Order;
import com.school.storeapplication.domain.order.OrderItem;
import com.school.storeapplication.domain.user.User;
import com.school.storeapplication.dto.OrderDto;
import com.school.storeapplication.mapper.OrderMapper;
import com.school.storeapplication.repo.CartRepository;
import com.school.storeapplication.repo.OrderRepository;
import com.school.storeapplication.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final CartRepository carts;
    private final UserRepository users;
    private final OrderMapper mapper;

    public OrderService(OrderRepository o, CartRepository c, UserRepository u, OrderMapper m) {
        this.orders = o; this.carts = c; this.users = u; this.mapper = m;
    }

    @Transactional
    public OrderDto checkout(String email) {
        for (int attempt = 1; ; attempt++) {
            try {
                return doCheckout(email);  // your current logic moved into a private method
            } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
                if (attempt >= 3) throw e; // give up after retries
            }
        }
    }
    @Transactional
    public OrderDto doCheckout(String email) {
        User u = users.findByEmail(email).orElseThrow();
        var cart = carts.findByUser(u).orElseThrow();
        if (cart.getItems().isEmpty()) throw new IllegalStateException("Cart is empty");

        var order = Order.builder().user(u).createdAt(Instant.now()).build();
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (var ci : cart.getItems()) {
            Product product = ci.getProduct();
            if (product.getStock() < ci.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for " + product.getName());
            }

            // Decrease stock
            product.setStock(product.getStock() - ci.getQuantity());

            var line = product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(line);
            items.add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(ci.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build());
        }

        order.setTotal(total);
        order.setItems(items);
        var saved = orders.save(order);

        // Clear cart
        cart.getItems().clear();
        carts.save(cart);

        var itemDtos = saved.getItems().stream().map(mapper::toDto).toList();
        return new OrderDto(saved.getId(), saved.getTotal(), saved.getCreatedAt(), itemDtos);
    }


    @Transactional(readOnly = true)
    public List<OrderDto> history(String email) {
        var list = orders.findByUserEmailOrderByCreatedAtDesc(email);
        var result = new ArrayList<OrderDto>(list.size());
        for (var o : list) {
            var items = o.getItems().stream().map(mapper::toDto).toList();
            result.add(new OrderDto(o.getId(), o.getTotal(), o.getCreatedAt(), items));
        }
        return result;
    }

    @Transactional
    public void cancel(String email, Long id) {
        var o = orders.findById(id).orElseThrow();
        if (!o.getUser().getEmail().equals(email)) throw new IllegalArgumentException("Forbidden");
        if (o.getCreatedAt().isBefore(Instant.now().minusSeconds(900))) // 15 min
            throw new IllegalStateException("Cancellation window passed");
        // restock
        o.getItems().forEach(i -> {
            var p = i.getProduct();
            p.setStock(p.getStock() + i.getQuantity());
        });
        orders.delete(o);
    }

}
