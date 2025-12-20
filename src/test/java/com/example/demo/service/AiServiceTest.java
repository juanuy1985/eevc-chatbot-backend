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

    private static final String TEST_API_KEY = "sk-1234567890123456789012345678901234567890123456789012";

    @Mock
    private ProductRepository productRepository;

    private AiService aiService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Create AiService with a valid-looking API key to bypass validation
        aiService = new AiService(TEST_API_KEY, productRepository);
    }

    @Test
    void testParseAiResponse_ReturnsFullProductDetails() throws Exception {
        // Setup mock products
        Product perno1 = new Product("P-001", "perno", "Perno Hexagonal 1/4\" x 2\" Acero Zincado", 1200, 0.45, 0.38);
        Product perno2 = new Product("P-002", "perno", "Perno Hexagonal 1/4\" x 4\" Acero Zincado", 950, 0.60, 0.50);
        Product volanda1 = new Product("V-005", "volanda", "Volanda Plana M8 Inoxidable", 1500, 0.25, 0.21);
        Product volanda2 = new Product("V-004", "volanda", "Volanda de Presión 3/8\"", 2200, 0.16, 0.13);

        // Mock findByProductNameKeywords to return specific products based on keywords
        when(productRepository.findByProductNameKeywords(any())).thenReturn(Arrays.asList(perno1, perno2, volanda1, volanda2));

        // Simulate AI response JSON with specific product keywords
        String aiResponseJson = "{"
                + "\"requestType\": \"request_info\","
                + "\"productKeywords\": [\"1/4\\\" x 2\\\"\", \"1/4\\\" x 4\\\"\", \"M8\", \"presión 3/8\"],"
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
        when(productRepository.findByProductNameKeywords(any())).thenReturn(Arrays.asList());

        // Simulate AI response JSON with products
        String aiResponseJson = "{"
                + "\"requestType\": \"request_info\","
                + "\"productKeywords\": [\"nonexistent\"],"
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

    @Test
    void testParseAiResponse_WithSpecificKeywords_ReturnsOnlyMatchingProducts() throws Exception {
        // Setup mock products - only return products that match specific keywords
        Product perno1 = new Product("P-001", "perno", "Perno Hexagonal 1/4\" x 2\" Acero Zincado", 1200, 0.45, 0.38);
        Product perno2 = new Product("P-002", "perno", "Perno Hexagonal 1/4\" x 4\" Acero Zincado", 950, 0.60, 0.50);
        Product volanda1 = new Product("V-005", "volanda", "Volanda Plana M8 Inoxidable", 1500, 0.25, 0.21);
        Product volanda2 = new Product("V-004", "volanda", "Volanda de Presión 3/8\"", 2200, 0.16, 0.13);

        // Mock to return only the 4 specific products that match the keywords
        when(productRepository.findByProductNameKeywords(any())).thenReturn(Arrays.asList(perno1, perno2, volanda1, volanda2));

        // Simulate AI response with specific keywords
        String aiResponseJson = "{"
                + "\"requestType\": \"request_info\","
                + "\"productKeywords\": [\"1/4\\\" x 2\\\"\", \"1/4\\\" x 4\\\"\", \"M8\", \"presión 3/8\"],"
                + "\"message\": \"Estoy recuperando la información de precios y stock\""
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
        
        // Should contain exactly 4 products matching the keywords
        assertEquals(4, products.size());
        
        // Verify the correct products are returned
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("P-001")));
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("P-002")));
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("V-005")));
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("V-004")));
        
        // Verify we DON'T have other products like P-003, P-004, etc.
        assertFalse(products.stream().anyMatch(p -> p.getCodigoProducto().equals("P-003")));
        assertFalse(products.stream().anyMatch(p -> p.getCodigoProducto().equals("V-001")));
    }
}
