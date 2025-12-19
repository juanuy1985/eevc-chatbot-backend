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
}
