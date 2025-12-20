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
        // Setup mock products - this is the complete product list
        Product perno1 = new Product("P-001", "perno", "Perno Hexagonal 1/4\" x 2\" Acero Zincado", 1200, 0.45, 0.38);
        Product perno2 = new Product("P-002", "perno", "Perno Hexagonal 1/4\" x 4\" Acero Zincado", 950, 0.60, 0.50);
        Product volanda1 = new Product("V-005", "volanda", "Volanda Plana M8 Inoxidable", 1500, 0.25, 0.21);
        Product volanda2 = new Product("V-004", "volanda", "Volanda de Presión 3/8\"", 2200, 0.16, 0.13);
        Product perno3 = new Product("P-003", "perno", "Perno Hexagonal 3/8\" x 3\" Alta Resistencia", 600, 0.85, 0.72);
        
        List<Product> allProducts = Arrays.asList(perno1, perno2, volanda1, volanda2, perno3);

        // Simulate AI response JSON with specific product codes (new format)
        String aiResponseJson = "{"
                + "\"requestType\": \"request_info\","
                + "\"productCodes\": [\"P-001\", \"P-002\", \"V-005\", \"V-004\"],"
                + "\"message\": \"Estoy recuperando la información de precios y stock\""
                + "}";

        // Use reflection to call private parseAiResponse method with new signature
        Method parseMethod = AiService.class.getDeclaredMethod("parseAiResponse", String.class, String.class, List.class);
        parseMethod.setAccessible(true);
        ChatResponse response = (ChatResponse) parseMethod.invoke(aiService, aiResponseJson, "CLI-002", allProducts);

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
        
        // Should contain 4 products (2 pernos + 2 volandas), excluding P-003
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
        // Setup mock products - complete list but AI returns empty product codes
        Product perno1 = new Product("P-001", "perno", "Perno Hexagonal 1/4\" x 2\" Acero Zincado", 1200, 0.45, 0.38);
        List<Product> allProducts = Arrays.asList(perno1);

        // Simulate AI response JSON with empty product codes
        String aiResponseJson = "{"
                + "\"requestType\": \"request_info\","
                + "\"productCodes\": [],"
                + "\"message\": \"Searching for products\""
                + "}";

        // Use reflection to call private parseAiResponse method with new signature
        Method parseMethod = AiService.class.getDeclaredMethod("parseAiResponse", String.class, String.class, List.class);
        parseMethod.setAccessible(true);
        ChatResponse response = (ChatResponse) parseMethod.invoke(aiService, aiResponseJson, "CLI-002", allProducts);

        // Assertions
        assertNotNull(response);
        Map<String, Object> information = response.getInformation();
        @SuppressWarnings("unchecked")
        List<Product> products = (List<Product>) information.get("response");
        
        // Should be empty since no product codes provided
        assertEquals(0, products.size());
    }

    @Test
    void testParseAiResponse_WithSpecificCodes_ReturnsOnlyMatchingProducts() throws Exception {
        // Setup all mock products
        Product perno1 = new Product("P-001", "perno", "Perno Hexagonal 1/4\" x 2\" Acero Zincado", 1200, 0.45, 0.38);
        Product perno2 = new Product("P-002", "perno", "Perno Hexagonal 1/4\" x 4\" Acero Zincado", 950, 0.60, 0.50);
        Product perno3 = new Product("P-003", "perno", "Perno Hexagonal 3/8\" x 3\" Alta Resistencia", 600, 0.85, 0.72);
        Product volanda1 = new Product("V-005", "volanda", "Volanda Plana M8 Inoxidable", 1500, 0.25, 0.21);
        Product volanda2 = new Product("V-004", "volanda", "Volanda de Presión 3/8\"", 2200, 0.16, 0.13);
        Product volanda3 = new Product("V-001", "volanda", "Volanda Plana 1/4\" Acero Zincado", 4000, 0.10, 0.08);

        // Complete product list
        List<Product> allProducts = Arrays.asList(perno1, perno2, perno3, volanda1, volanda2, volanda3);

        // Simulate AI response with specific product codes (AI filtered to only these 4)
        String aiResponseJson = "{"
                + "\"requestType\": \"request_info\","
                + "\"productCodes\": [\"P-001\", \"P-002\", \"V-005\", \"V-004\"],"
                + "\"message\": \"Estoy recuperando la información de precios y stock\""
                + "}";

        // Use reflection to call private parseAiResponse method with new signature
        Method parseMethod = AiService.class.getDeclaredMethod("parseAiResponse", String.class, String.class, List.class);
        parseMethod.setAccessible(true);
        ChatResponse response = (ChatResponse) parseMethod.invoke(aiService, aiResponseJson, "CLI-002", allProducts);

        // Assertions
        assertNotNull(response);
        Map<String, Object> information = response.getInformation();
        @SuppressWarnings("unchecked")
        List<Product> products = (List<Product>) information.get("response");
        
        // Should contain exactly 4 products matching the codes returned by AI
        assertEquals(4, products.size());
        
        // Verify the correct products are returned
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("P-001")));
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("P-002")));
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("V-005")));
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("V-004")));
        
        // Verify we DON'T have other products like P-003, V-001 that were not in the AI response
        assertFalse(products.stream().anyMatch(p -> p.getCodigoProducto().equals("P-003")));
        assertFalse(products.stream().anyMatch(p -> p.getCodigoProducto().equals("V-001")));
    }

    @Test
    void testParseAiResponse_WithUnknownRequestType() throws Exception {
        // Setup mock products
        Product perno1 = new Product("P-001", "perno", "Perno Hexagonal 1/4\" x 2\" Acero Zincado", 1200, 0.45, 0.38);
        List<Product> allProducts = Arrays.asList(perno1);

        // Simulate AI response with unknown requestType
        String aiResponseJson = "{"
                + "\"requestType\": \"unknown\","
                + "\"message\": \"Lo siento, no logré entender tu solicitud. ¿Puedes proporcionar más detalles o intentar reformular tu pedido?\""
                + "}";

        // Use reflection to call private parseAiResponse method
        Method parseMethod = AiService.class.getDeclaredMethod("parseAiResponse", String.class, String.class, List.class);
        parseMethod.setAccessible(true);
        ChatResponse response = (ChatResponse) parseMethod.invoke(aiService, aiResponseJson, "CLI-002", allProducts);

        // Assertions
        assertNotNull(response);
        assertEquals("CLI-002", response.getClient());
        assertEquals("Lo siento, no logré entender tu solicitud. ¿Puedes proporcionar más detalles o intentar reformular tu pedido?", response.getResponseMessage());

        Map<String, Object> information = response.getInformation();
        assertNotNull(information);
        assertEquals("unknown", information.get("type"));
        assertEquals("Lo siento, no logré entender tu solicitud. ¿Puedes proporcionar más detalles o intentar reformular tu pedido?", information.get("response"));
    }

    @Test
    void testParseAiResponse_WithInvalidJson() throws Exception {
        // Setup mock products
        Product perno1 = new Product("P-001", "perno", "Perno Hexagonal 1/4\" x 2\" Acero Zincado", 1200, 0.45, 0.38);
        List<Product> allProducts = Arrays.asList(perno1);

        // Simulate invalid JSON response
        String aiResponseJson = "This is not valid JSON at all";

        // Use reflection to call private parseAiResponse method
        Method parseMethod = AiService.class.getDeclaredMethod("parseAiResponse", String.class, String.class, List.class);
        parseMethod.setAccessible(true);
        ChatResponse response = (ChatResponse) parseMethod.invoke(aiService, aiResponseJson, "CLI-002", allProducts);

        // Assertions
        assertNotNull(response);
        assertEquals("CLI-002", response.getClient());
        assertEquals("Lo siento, no logré entender tu solicitud. ¿Puedes proporcionar más detalles o intentar reformular tu pedido?", response.getResponseMessage());

        Map<String, Object> information = response.getInformation();
        assertNotNull(information);
        assertEquals("unknown", information.get("type"));
        assertEquals("Lo siento, no logré entender tu solicitud. ¿Puedes proporcionar más detalles o intentar reformular tu pedido?", information.get("response"));
    }
}
