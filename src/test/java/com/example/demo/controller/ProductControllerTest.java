package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void testGetProductsByType_WithMatchingProducts() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(
                new Product("001", "perno", "Perno Hexagonal 1/4 * 4", 1000),
                new Product("002", "perno", "Perno Hexagonal 1/4 * 5", 800)
        );
        when(productService.getProductsByType("perno")).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/products/perno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].codigoProducto").value("001"))
                .andExpect(jsonPath("$[0].tipoProducto").value("perno"))
                .andExpect(jsonPath("$[0].nombreProducto").value("Perno Hexagonal 1/4 * 4"))
                .andExpect(jsonPath("$[0].cantidadStock").value(1000))
                .andExpect(jsonPath("$[1].codigoProducto").value("002"))
                .andExpect(jsonPath("$[1].tipoProducto").value("perno"))
                .andExpect(jsonPath("$[1].nombreProducto").value("Perno Hexagonal 1/4 * 5"))
                .andExpect(jsonPath("$[1].cantidadStock").value(800));
    }

    @Test
    void testGetProductsByType_WithNoMatchingProducts() throws Exception {
        // Arrange
        when(productService.getProductsByType("tornillo")).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/products/tornillo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
