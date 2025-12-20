package com.example.demo.repository;

import com.example.demo.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindByTipoProducto_Perno() {
        List<Product> pernos = productRepository.findByTipoProducto("perno");
        
        assertNotNull(pernos);
        assertFalse(pernos.isEmpty());
        assertEquals(6, pernos.size());
        
        // Verify all products are of type "perno"
        for (Product product : pernos) {
            assertEquals("perno", product.getTipoProducto());
        }
        
        // Verify first product details
        Product firstPerno = pernos.get(0);
        assertEquals("P-001", firstPerno.getCodigoProducto());
        assertEquals("Perno Hexagonal 1/4\" x 2\" Acero Zincado", firstPerno.getNombreProducto());
        assertEquals(1200, firstPerno.getCantidadStock());
        assertEquals(0.45, firstPerno.getPrecioUnitario());
        assertEquals(0.38, firstPerno.getPrecioXMayor());
    }

    @Test
    void testFindByTipoProducto_Tuerca() {
        List<Product> tuercas = productRepository.findByTipoProducto("tuerca");
        
        assertNotNull(tuercas);
        assertFalse(tuercas.isEmpty());
        assertEquals(6, tuercas.size());
        
        // Verify all products are of type "tuerca"
        for (Product product : tuercas) {
            assertEquals("tuerca", product.getTipoProducto());
        }
        
        // Verify first product details
        Product firstTuerca = tuercas.get(0);
        assertEquals("T-001", firstTuerca.getCodigoProducto());
        assertEquals("Tuerca Hexagonal 1/4\" Acero Zincado", firstTuerca.getNombreProducto());
        assertEquals(3000, firstTuerca.getCantidadStock());
        assertEquals(0.15, firstTuerca.getPrecioUnitario());
        assertEquals(0.12, firstTuerca.getPrecioXMayor());
    }

    @Test
    void testFindByTipoProducto_Volanda() {
        List<Product> volandas = productRepository.findByTipoProducto("volanda");
        
        assertNotNull(volandas);
        assertFalse(volandas.isEmpty());
        assertEquals(6, volandas.size());
        
        // Verify all products are of type "volanda"
        for (Product product : volandas) {
            assertEquals("volanda", product.getTipoProducto());
        }
        
        // Verify first product details
        Product firstVolanda = volandas.get(0);
        assertEquals("V-001", firstVolanda.getCodigoProducto());
        assertEquals("Volanda Plana 1/4\" Acero Zincado", firstVolanda.getNombreProducto());
        assertEquals(4000, firstVolanda.getCantidadStock());
        assertEquals(0.10, firstVolanda.getPrecioUnitario());
        assertEquals(0.08, firstVolanda.getPrecioXMayor());
    }

    @Test
    void testFindByTipoProducto_NonExistent() {
        List<Product> products = productRepository.findByTipoProducto("arandela");
        
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void testFindAll() {
        List<Product> allProducts = productRepository.findAll();
        
        assertNotNull(allProducts);
        // Total: 6 pernos + 6 tuercas + 6 volandas = 18 products
        assertEquals(18, allProducts.size());
    }

    @Test
    void testFindByProductNameKeywords_SingleKeyword() {
        List<String> keywords = List.of("1/4\" x 2\"");
        List<Product> products = productRepository.findByProductNameKeywords(keywords);
        
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("P-001", products.get(0).getCodigoProducto());
        assertEquals("Perno Hexagonal 1/4\" x 2\" Acero Zincado", products.get(0).getNombreProducto());
    }

    @Test
    void testFindByProductNameKeywords_MultipleKeywords() {
        List<String> keywords = List.of("1/4\" x 2\"", "1/4\" x 4\"", "Plana M8", "Presión 3/8");
        List<Product> products = productRepository.findByProductNameKeywords(keywords);
        
        assertNotNull(products);
        // Should match: P-001 (1/4" x 2"), P-002 (1/4" x 4"), V-005 (Plana M8), V-004 (Presión 3/8")
        assertEquals(4, products.size());
        
        // Verify specific products are found
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("P-001")));
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("P-002")));
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("V-005")));
        assertTrue(products.stream().anyMatch(p -> p.getCodigoProducto().equals("V-004")));
    }

    @Test
    void testFindByProductNameKeywords_CaseInsensitive() {
        List<String> keywords = List.of("HEXAGONAL", "plana");
        List<Product> products = productRepository.findByProductNameKeywords(keywords);
        
        assertNotNull(products);
        assertFalse(products.isEmpty());
        
        // Should find products with "Hexagonal" or "Plana" in their names (case insensitive)
        for (Product product : products) {
            String nameLower = product.getNombreProducto().toLowerCase();
            assertTrue(nameLower.contains("hexagonal") || nameLower.contains("plana"),
                    "Product name should contain either 'hexagonal' or 'plana': " + product.getNombreProducto());
        }
    }

    @Test
    void testFindByProductNameKeywords_NoMatch() {
        List<String> keywords = List.of("nonexistent", "invalid");
        List<Product> products = productRepository.findByProductNameKeywords(keywords);
        
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void testFindByProductNameKeywords_EmptyKeywords() {
        List<String> keywords = List.of();
        List<Product> products = productRepository.findByProductNameKeywords(keywords);
        
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }
}
