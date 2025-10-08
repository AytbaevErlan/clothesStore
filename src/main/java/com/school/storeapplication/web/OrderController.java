package com.school.storeapplication.web;

import com.school.storeapplication.dto.OrderDto;
import com.school.storeapplication.service.OrderService;
import com.school.storeapplication.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@PreAuthorize("hasRole('BUYER')")
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orders; private final PaymentService payments;
    public OrderController(OrderService o, PaymentService p) { this.orders = o; this.payments = p; }


    @PostMapping("/checkout")
    public OrderDto checkout(@AuthenticationPrincipal UserDetails ud) {
        var order = orders.checkout(ud.getUsername());
        payments.createMock(order.id());
        return order;
    }


    @GetMapping
    public List<OrderDto> history(@AuthenticationPrincipal UserDetails ud) { return orders.history(ud.getUsername()); }
    @DeleteMapping("/{orderId}")
    public void cancel(@AuthenticationPrincipal UserDetails ud, @PathVariable Long orderId) {
        orders.cancel(ud.getUsername(), orderId);
    }

}
