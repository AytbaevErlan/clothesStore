package com.school.storeapplication.web;

import com.school.storeapplication.dto.AuthResponse;
import com.school.storeapplication.dto.LoginRequest;
import com.school.storeapplication.dto.RegisterRequest;
import com.school.storeapplication.security.JwtUtils;
import com.school.storeapplication.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager am;
    private final JwtUtils jwt;
    private final UserService users;

    public AuthController(AuthenticationManager am, JwtUtils jwt, UserService users) {
        this.am = am; this.jwt = jwt; this.users = users;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Validated @RequestBody RegisterRequest r) {
        var u = users.registerBuyer(r);
        var roles = u.getRoles().stream().map(Enum::name).toList();
        var token = jwt.generate(u.getEmail(), roles);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest r) {
        Authentication auth = am.authenticate(new UsernamePasswordAuthenticationToken(r.email(), r.password()));
        var principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        var roles = principal.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        var token = jwt.generate(r.email(), roles);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
