package com.school.storeapplication.repo;

import com.school.storeapplication.domain.cart.Cart;
import com.school.storeapplication.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
