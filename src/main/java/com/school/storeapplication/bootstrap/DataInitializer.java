package com.school.storeapplication.bootstrap;


import com.school.storeapplication.domain.Role;
import com.school.storeapplication.domain.catalog.Category;
import com.school.storeapplication.domain.catalog.Product;
import com.school.storeapplication.domain.user.User;
import com.school.storeapplication.repo.CategoryRepository;
import com.school.storeapplication.repo.ProductRepository;
import com.school.storeapplication.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.util.Set;
@Configuration
public class DataInitializer {
    @Bean CommandLineRunner seed(CategoryRepository cats, ProductRepository products, UserRepository users, PasswordEncoder pe) {
        return args -> {
            if (cats.count() == 0) {
                var men = cats.save(Category.builder().name("Men").build());
                var women = cats.save(Category.builder().name("Women").build());
                products.save(Product.builder().name("Classic T-Shirt").description("100% cotton").price(new BigDecimal("19.99")).sku("TSHIRT-001").stock(100).imageUrl("https://picsum.photos/200/300").category(men).build());
                products.save(Product.builder().name("Denim Jacket").description("Slim fit").price(new BigDecimal("59.99")).sku("JACKET-001").stock(50).imageUrl("https://picsum.photos/200/301").category(women).build());
            }
            if (users.count() == 0) {
                users.save(User.builder().email("buyer@example.com").password(pe.encode("password")).firstName("Buyer").lastName("User").roles(Set.of(Role.ROLE_BUYER)).build());
            }
            if (users.findByEmail("admin@store.com").isEmpty()) {
                var admin = User.builder()
                        .email("admin@store.com")
                        .password(pe.encode("Admin#123"))
                        .roles(Set.of(Role.ROLE_ADMIN))
                        .build();
                users.save(admin);
            }

        };
    }
}
