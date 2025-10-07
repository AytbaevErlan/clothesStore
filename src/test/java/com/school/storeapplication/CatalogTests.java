package com.school.storeapplication;

import com.school.storeapplication.repo.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class CatalogTests {
    @Autowired MockMvc mvc;
    @Test void productsListAccessible() throws Exception {
        mvc.perform(get("/api/catalog/products")).andExpect(status().isOk());
    }
}
