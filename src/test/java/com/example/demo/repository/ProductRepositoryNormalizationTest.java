package com.example.demo.repository;

import com.example.demo.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryNormalizationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindByProductNameKeywords_WithVariations() {
        // Test that "1/4x2" (without quotes/spaces) matches "1/4\" x 2\"" (with quotes/spaces)
        List<String> keywords1 = List.of("1/4x2");
        List<Product> products1 = productRepository.findByProductNameKeywords(keywords1);
        
        assertNotNull(products1);
        assertTrue(products1.size() > 0, "Should find products with '1/4 x 2' even when searching for '1/4x2'");
        assertTrue(products1.stream().anyMatch(p -> p.getCodigoProducto().equals("P-001")),
                "Should match P-001 (Perno Hexagonal 1/4\" x 2\") when searching for '1/4x2'");
    }

    @Test
    void testFindByProductNameKeywords_CaseAndQuoteVariations() {
        // Test various input formats that should all match the same products
        List<String> keywords = List.of("1/4x2", "1/4 x 2", "1/4\" x 2\"", "M8", "m8", "presion", "presión");
        List<Product> products = productRepository.findByProductNameKeywords(keywords);
        
        assertNotNull(products);
        // Should find at least P-001 (1/4 x 2), V-005 (M8), and V-004 (presión)
        assertTrue(products.size() >= 3, "Should find multiple products with various keyword formats");
        
        // Verify specific matches
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("P-001")),
                "Should match P-001 with various '1/4x2' formats");
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("V-005")),
                "Should match V-005 with 'M8' or 'm8'");
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("V-004")),
                "Should match V-004 with 'presion' or 'presión'");
    }

    @Test
    void testFindByProductNameKeywords_SpaceVariations() {
        // Test that "3 / 8" (with spaces) matches "3/8\"" (without spaces)
        List<String> keywords = List.of("3 / 8");
        List<Product> products = productRepository.findByProductNameKeywords(keywords);
        
        assertNotNull(products);
        assertTrue(products.size() > 0, "Should find products with '3/8' even when searching for '3 / 8'");
        assertTrue(products.stream().anyMatch(p -> 
                java.util.Set.of("V-004", "V-002").contains(p.getCodigoProducto())),
                "Should match products with '3/8' in name");
    }
}
