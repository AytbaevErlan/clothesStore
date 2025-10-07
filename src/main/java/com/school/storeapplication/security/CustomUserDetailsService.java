package com.school.storeapplication.security;

import com.school.storeapplication.repo.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public CustomUserDetailsService(UserRepository users) { this.users = users; }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var u = users.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Not found"));
        var auths = u.getRoles().stream().map(r -> "ROLE_" + r.name().replace("ROLE_", ""))
                .map(SimpleGrantedAuthority::new).toList();
        return new org.springframework.security.core.userdetails.User(u.getEmail(), u.getPassword(), auths);
    }
}
