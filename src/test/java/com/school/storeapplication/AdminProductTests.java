package com.school.storeapplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.storeapplication.dto.ProductDto;
import com.school.storeapplication.repo.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.school.storeapplication.security.JwtUtils;
import java.util.List;


import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminProductTests {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired ProductRepository products;

    @Autowired JwtUtils jwt;

    String bearer() {
        String token = jwt.generate("admin@store.com", List.of("ROLE_ADMIN"));
        return "Bearer " + token;
    }

    Long createdId;
    String createdSku;

    private ProductDto dtoNewHoodie() {
        createdSku = "HOODIE-SELL-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
        return new ProductDto(
                null,                              // id
                "Seller Hoodie",                   // name
                "Warm",                            // description
                new BigDecimal("49.99"),           // price
                createdSku,                        // sku (unique each run)
                Integer.valueOf(25),               // stock
                "https://picsum.photos/200/400",   // imageUrl
                3L,                                // categoryId (Men)
                "Men",                             // categoryName (optional)
                Boolean.TRUE,                      // active
                null,                              // sellerId
                null                               // sellerEmail
        );
    }

    private ProductDto dtoExisting(long id) {
        return new ProductDto(
                id,
                "Admin Jacket",
                "Desc",
                new BigDecimal("59.99"),
                "ADMIN-" + UUID.randomUUID().toString().substring(0,6).toUpperCase(),
                Integer.valueOf(10),
                "https://picsum.photos/200/400",
                3L,
                "Men",
                Boolean.TRUE,
                null,
                null
        );
    }

    @AfterEach
    void cleanup() {
        try { if (createdId != null) products.deleteById(createdId); } catch (Exception ignored) {}
        try { if (createdSku != null) products.findBySku(createdSku).ifPresent(p -> products.deleteById(p.getId())); } catch (Exception ignored) {}
        createdId = null;
        createdSku = null;
    }

    @Test
    void admin_can_create_product() throws Exception {
        var body = om.writeValueAsString(dtoNewHoodie());

        var res = mvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearer())
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var json = om.readTree(res.getResponse().getContentAsString());
        createdId = json.get("id").asLong();
        assertThat(createdId).isNotNull();
    }

    @Test
    void admin_can_update_product() throws Exception {
        // create first
        var create = mvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearer())
                        .content(om.writeValueAsString(dtoNewHoodie())))
                .andExpect(status().isCreated())
                .andReturn();
        createdId = om.readTree(create.getResponse().getContentAsString()).get("id").asLong();
        assertThat(createdId).as("[createdId from previous test]").isNotNull();

        // update it
        var updateDto = dtoExisting(createdId);
        mvc.perform(put("/api/admin/products/{id}", createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearer())
                        .content(om.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admin Jacket"));
    }
}
