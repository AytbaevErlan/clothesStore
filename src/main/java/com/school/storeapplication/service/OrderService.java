package com.school.storeapplication.service;

import com.school.storeapplication.domain.order.Order;
import com.school.storeapplication.domain.order.OrderItem;
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
        var u = users.findByEmail(email).orElseThrow();
        var cart = carts.findByUser(u).orElseThrow();
        if (cart.getItems().isEmpty()) throw new IllegalStateException("cart empty");

        var order = Order.builder().user(u).createdAt(Instant.now()).build();
        var items = new ArrayList<OrderItem>();
        BigDecimal total = BigDecimal.ZERO;

        for (var ci : cart.getItems()) {
            var p = ci.getProduct();
            int qty = ci.getQuantity();
            if (p.getStock() < qty) throw new IllegalStateException("insufficient stock for product " + p.getId());

            p.setStock(p.getStock() - qty);
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(qty)));

            items.add(OrderItem.builder()
                    .order(order)
                    .product(p)
                    .quantity(qty)
                    .priceAtPurchase(p.getPrice())
                    .build());
        }

        order.setTotal(total);
        order.setItems(items);

        var saved = orders.save(order);   // cascades items
        cart.getItems().clear();          // requires orphanRemoval on Cart.items
        carts.save(cart);

        // Build DTO manually (we only map OrderItem -> OrderItemDto via MapStruct)
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
}
