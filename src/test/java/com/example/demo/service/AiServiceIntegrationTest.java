package com.example.demo.service;

import com.example.demo.model.ChatResponse;
import com.example.demo.model.Client;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify that the keyword-based product search
 * returns only the requested products, not all products of a type.
 * 
 * This test simulates the scenario described in the issue where
 * the user requests specific products like "Perno Hexagonal 1/4x2, 1/4x4,
 * volandas Planas M8, de presion 3/8" and expects only those 4 products
 * in the response, not all pernos and volandas.
 */
@SpringBootTest
class AiServiceIntegrationTest {

    private static final String TEST_API_KEY = "sk-1234567890123456789012345678901234567890123456789012";

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testProductRepository_FindsBySpecificKeywords_NotAllTypes() {
        // This simulates the actual user request from the issue:
        // "Perno Hexagonal 1/4x2, 1/4x4, volandas Planas M8, de presion 3/8"
        
        // The AI should extract these specific keywords from the user message
        List<String> keywords = List.of("1/4\" x 2\"", "1/4\" x 4\"", "Plana M8", "Presión 3/8");
        
        // Search for products matching these keywords
        List<Product> matchedProducts = productRepository.findByProductNameKeywords(keywords);
        
        // Verify we get only 4 specific products, not all 6 pernos and 6 volandas
        assertNotNull(matchedProducts);
        assertEquals(4, matchedProducts.size(), 
                "Should return exactly 4 products matching the specific keywords, not all products of those types");
        
        // Verify we have the correct products
        assertTrue(matchedProducts.stream().anyMatch(p -> 
                p.getCodigoProducto().equals("P-001") && 
                p.getNombreProducto().contains("1/4\" x 2\"")),
                "Should include Perno Hexagonal 1/4\" x 2\"");
        
        assertTrue(matchedProducts.stream().anyMatch(p -> 
                p.getCodigoProducto().equals("P-002") && 
                p.getNombreProducto().contains("1/4\" x 4\"")),
                "Should include Perno Hexagonal 1/4\" x 4\"");
        
        assertTrue(matchedProducts.stream().anyMatch(p -> 
                p.getCodigoProducto().equals("V-005") && 
                p.getNombreProducto().contains("M8")),
                "Should include Volanda Plana M8");
        
        assertTrue(matchedProducts.stream().anyMatch(p -> 
                p.getCodigoProducto().equals("V-004") && 
                p.getNombreProducto().contains("Presión 3/8\"")),
                "Should include Volanda de Presión 3/8\"");
        
        // Verify we DON'T have other pernos or volandas
        assertFalse(matchedProducts.stream().anyMatch(p -> p.getCodigoProducto().equals("P-003")),
                "Should NOT include Perno Hexagonal 3/8\" x 3\" (not requested)");
        assertFalse(matchedProducts.stream().anyMatch(p -> p.getCodigoProducto().equals("P-004")),
                "Should NOT include Perno Carrocero (not requested)");
        assertFalse(matchedProducts.stream().anyMatch(p -> p.getCodigoProducto().equals("P-005")),
                "Should NOT include Perno Allen M8 (not requested)");
        assertFalse(matchedProducts.stream().anyMatch(p -> p.getCodigoProducto().equals("P-006")),
                "Should NOT include Perno Allen M10 (not requested)");
        assertFalse(matchedProducts.stream().anyMatch(p -> p.getCodigoProducto().equals("V-001")),
                "Should NOT include Volanda Plana 1/4\" (not requested)");
        assertFalse(matchedProducts.stream().anyMatch(p -> p.getCodigoProducto().equals("V-002")),
                "Should NOT include Volanda Plana 3/8\" (not requested)");
        assertFalse(matchedProducts.stream().anyMatch(p -> p.getCodigoProducto().equals("V-003")),
                "Should NOT include Volanda de Presión 1/4\" (not requested)");
        assertFalse(matchedProducts.stream().anyMatch(p -> p.getCodigoProducto().equals("V-006")),
                "Should NOT include Volanda Plana M10 (not requested)");
    }

    @Test
    void testParseAiResponse_WithRealRepository_ReturnsOnlyMatchingProducts() throws Exception {
        // Create a minimal AiService instance for testing parseAiResponse
        // We use reflection to test the private parseAiResponse method
        AiService aiService = new AiService(TEST_API_KEY, productRepository);
        
        // Get all products from the real repository (this is what the new strategy does)
        List<Product> allProducts = productRepository.findAll();
        
        // Simulate an AI response with specific product codes
        // This is what the AI would return after analyzing the user request and filtering from all products
        String aiResponseJson = "{"
                + "\"requestType\": \"request_info\","
                + "\"productCodes\": [\"P-001\", \"P-002\", \"V-005\", \"V-004\"],"
                + "\"message\": \"Estoy recuperando la información de precios y stock\""
                + "}";
        
        // Use reflection to call the private parseAiResponse method with new signature
        Method parseMethod = AiService.class.getDeclaredMethod("parseAiResponse", String.class, String.class, List.class);
        parseMethod.setAccessible(true);
        ChatResponse response = (ChatResponse) parseMethod.invoke(aiService, aiResponseJson, "CLI-002", allProducts);
        
        // Verify the response
        assertNotNull(response);
        assertEquals("CLI-002", response.getClient());
        assertEquals("Estoy recuperando la información de precios y stock", response.getResponseMessage());
        
        Map<String, Object> information = response.getInformation();
        assertNotNull(information);
        assertEquals("request_info", information.get("type"));
        
        @SuppressWarnings("unchecked")
        List<Product> products = (List<Product>) information.get("response");
        
        // The critical assertion: we should get exactly 4 products, not 12 (all pernos and volandas)
        assertEquals(4, products.size(), 
                "Response should contain exactly 4 products matching the specific codes, not all 12 pernos and volandas");
        
        // Verify product details are complete (as per the issue's expected response)
        for (Product product : products) {
            assertNotNull(product.getCodigoProducto(), "Product should have codigo");
            assertNotNull(product.getTipoProducto(), "Product should have tipo");
            assertNotNull(product.getNombreProducto(), "Product should have nombre");
            assertTrue(product.getCantidadStock() > 0, "Product should have stock");
            assertTrue(product.getPrecioUnitario() > 0, "Product should have precio unitario");
            assertTrue(product.getPrecioXMayor() > 0, "Product should have precio por mayor");
        }
        
        // Verify specific products from the issue's expected response
        assertTrue(products.stream().anyMatch(p -> 
                p.getCodigoProducto().equals("P-001") &&
                p.getCantidadStock() == 1200 &&
                p.getPrecioUnitario() == 0.45 &&
                p.getPrecioXMayor() == 0.38),
                "Should include P-001 with correct details");
        
        assertTrue(products.stream().anyMatch(p -> 
                p.getCodigoProducto().equals("P-002") &&
                p.getCantidadStock() == 950 &&
                p.getPrecioUnitario() == 0.60 &&
                p.getPrecioXMayor() == 0.50),
                "Should include P-002 with correct details");
        
        assertTrue(products.stream().anyMatch(p -> 
                p.getCodigoProducto().equals("V-005") &&
                p.getCantidadStock() == 1500 &&
                p.getPrecioUnitario() == 0.25 &&
                p.getPrecioXMayor() == 0.21),
                "Should include V-005 with correct details");
        
        assertTrue(products.stream().anyMatch(p -> 
                p.getCodigoProducto().equals("V-004") &&
                p.getCantidadStock() == 2200 &&
                p.getPrecioUnitario() == 0.16 &&
                p.getPrecioXMayor() == 0.13),
                "Should include V-004 with correct details");
    }
}
