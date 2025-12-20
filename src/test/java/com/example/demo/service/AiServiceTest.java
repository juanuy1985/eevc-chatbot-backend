package com.example.demo.service;

import com.example.demo.model.ChatResponse;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AiServiceTest {

    @Mock
    private ProductRepository productRepository;

    private AiService aiService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Create AiService with a valid-looking API key to bypass validation
        aiService = new AiService("sk-1234567890123456789012345678901234567890123456789012", productRepository);
    }

    @Test
    void testParseAiResponse_ReturnsFullProductDetails() throws Exception {
        // Setup mock products
        Product perno1 = new Product("P-001", "perno", "Perno Hexagonal 1/4\" x 2\" Acero Zincado", 1200, 0.45, 0.38);
        Product perno2 = new Product("P-002", "perno", "Perno Hexagonal 1/4\" x 4\" Acero Zincado", 950, 0.60, 0.50);
        Product volanda1 = new Product("V-005", "volanda", "Volanda Plana M8 Inoxidable", 1500, 0.25, 0.21);
        Product volanda2 = new Product("V-004", "volanda", "Volanda de Presión 3/8\"", 2200, 0.16, 0.13);

        when(productRepository.findByTipoProducto("perno")).thenReturn(Arrays.asList(perno1, perno2));
        when(productRepository.findByTipoProducto("volanda")).thenReturn(Arrays.asList(volanda1, volanda2));

        // Simulate AI response JSON
        String aiResponseJson = "{"
                + "\"requestType\": \"request_info\","
                + "\"products\": [\"perno\", \"volanda\"],"
                + "\"message\": \"Estoy recuperando la información de precios y stock\""
                + "}";

        // Use reflection to call private parseAiResponse method
        Method parseMethod = AiService.class.getDeclaredMethod("parseAiResponse", String.class, String.class);
        parseMethod.setAccessible(true);
        ChatResponse response = (ChatResponse) parseMethod.invoke(aiService, aiResponseJson, "CLI-002");

        // Assertions
        assertNotNull(response);
        assertEquals("CLI-002", response.getClient());
        assertEquals("Estoy recuperando la información de precios y stock", response.getResponseMessage());

        Map<String, Object> information = response.getInformation();
        assertNotNull(information);
        assertEquals("request_info", information.get("type"));

        // Verify response contains Product objects, not just strings
        Object responseObj = information.get("response");
        assertNotNull(responseObj);
        assertTrue(responseObj instanceof List);

        @SuppressWarnings("unchecked")
        List<Product> products = (List<Product>) responseObj;
        
        // Should contain 4 products (2 pernos + 2 volandas)
        assertEquals(4, products.size());

        // Verify first product is a complete Product object with all fields
        Product firstProduct = products.get(0);
        assertEquals("P-001", firstProduct.getCodigoProducto());
        assertEquals("perno", firstProduct.getTipoProducto());
        assertEquals("Perno Hexagonal 1/4\" x 2\" Acero Zincado", firstProduct.getNombreProducto());
        assertEquals(1200, firstProduct.getCantidadStock());
        assertEquals(0.45, firstProduct.getPrecioUnitario());
        assertEquals(0.38, firstProduct.getPrecioXMayor());
    }

    @Test
    void testParseAiResponse_WithEmptyProducts() throws Exception {
        // Setup mock products - return empty list
        when(productRepository.findByTipoProducto(any())).thenReturn(Arrays.asList());

        // Simulate AI response JSON with products
        String aiResponseJson = "{"
                + "\"requestType\": \"request_info\","
                + "\"products\": [\"nonexistent\"],"
                + "\"message\": \"Searching for products\""
                + "}";

        // Use reflection to call private parseAiResponse method
        Method parseMethod = AiService.class.getDeclaredMethod("parseAiResponse", String.class, String.class);
        parseMethod.setAccessible(true);
        ChatResponse response = (ChatResponse) parseMethod.invoke(aiService, aiResponseJson, "CLI-002");

        // Assertions
        assertNotNull(response);
        Map<String, Object> information = response.getInformation();
        @SuppressWarnings("unchecked")
        List<Product> products = (List<Product>) information.get("response");
        
        // Should be empty since no products found
        assertEquals(0, products.size());
    }
}
