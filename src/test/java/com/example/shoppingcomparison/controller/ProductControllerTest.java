package com.example.shoppingcomparison.controller;

import com.example.shoppingcomparison.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class ProductControllerTest {
    private final ProductService productService;

    @Autowired
    public ProductControllerTest(ProductService productService) {
        this.productService = productService;
    }

    @Test
    public void testProductController() throws Exception {
        ProductController productController = new ProductController(productService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        mockMvc.perform(get("/allProducts")).andExpect(view().name("index.html"));
    }

}