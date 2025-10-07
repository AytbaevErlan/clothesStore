package com.school.storeapplication.service;

import  com.school.storeapplication.domain.Role;
import com.school.storeapplication.domain.user.User;
import  com.school.storeapplication.dto.RegisterRequest;
import  com.school.storeapplication.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository users; private final PasswordEncoder pe;
    public UserService(UserRepository users, PasswordEncoder pe) { this.users = users; this.pe = pe; }


    public User registerBuyer(RegisterRequest r) {
        if (users.existsByEmail(r.email())) throw new IllegalArgumentException("email in use");
        var u = User.builder()
                .email(r.email())
                .password(pe.encode(r.password()))
                .firstName(r.firstName())
                .lastName(r.lastName())
                .roles(Set.of(Role.ROLE_BUYER))
                .build();
        return users.save(u);
    }
}