package com.school.storeapplication.service;

import com.school.storeapplication.domain.order.Order;
import com.school.storeapplication.domain.payment.Payment;
import com.school.storeapplication.repo.OrderRepository;
import com.school.storeapplication.repo.PaymentRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;

import java.time.Instant;

@Service
public class PaymentService {
    private final PaymentRepository payments; private final OrderRepository orders;
    public PaymentService(PaymentRepository p, OrderRepository o) { this.payments = p; this.orders = o; }


    public Payment createMock(Long orderId) {
        Order o = orders.findById(orderId).orElseThrow();
        var pay = Payment.builder().order(o).provider("MOCK").status("CREATED").createdAt(Instant.now()).build();
        return payments.save(pay);
    }
}
