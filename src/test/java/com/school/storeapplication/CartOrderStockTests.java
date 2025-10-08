package com.school.storeapplication;

import com.school.storeapplication.repo.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartOrderStockTests {

    @Autowired MockMvc mvc;
    @Autowired ProductRepository products;

    String bearerUser() { return "Bearer " + System.getenv("USER_JWT"); }

    @Test
    void cannotExceedStockAndCheckoutDecrements() throws Exception {
        var p = products.findAll().get(0);
        int original = p.getStock();

        // set qty to original + 1 -> expect 400
        mvc.perform(patch("/api/cart/set/{id}", p.getId())
                        .header("Authorization", bearerUser())
                        .param("qty", String.valueOf(original + 1)))
                .andExpect(status().isBadRequest());

        // set to 1 -> ok
        mvc.perform(patch("/api/cart/set/{id}", p.getId())
                        .header("Authorization", bearerUser())
                        .param("qty", "1"))
                .andExpect(status().isOk());

        // checkout -> ok
        mvc.perform(post("/api/orders/checkout")
                        .header("Authorization", bearerUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(greaterThanOrEqualTo(1))));
    }
}
