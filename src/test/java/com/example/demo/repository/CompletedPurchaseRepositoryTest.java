package com.example.demo.repository;

import com.example.demo.model.CompletedPurchase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CompletedPurchaseRepositoryTest {

    @Autowired
    private CompletedPurchaseRepository purchaseRepository;

    @Test
    void testFindAll() {
        List<CompletedPurchase> purchases = purchaseRepository.findAll();
        
        assertNotNull(purchases);
        assertFalse(purchases.isEmpty());
    }

    @Test
    void testFindByCodigoCompra_ExistingPurchase() {
        Optional<CompletedPurchase> purchase = purchaseRepository.findByCodigoCompra("COMP-001");
        
        assertTrue(purchase.isPresent());
        assertEquals("COMP-001", purchase.get().getCodigoCompra());
        assertEquals("CLI-001", purchase.get().getCodigoCliente());
        assertEquals("Juan Carlos Rodríguez Pérez", purchase.get().getNombreCliente());
        assertEquals("2024-01-15", purchase.get().getFecha());
        assertNotNull(purchase.get().getProductos());
        assertFalse(purchase.get().getProductos().isEmpty());
        assertEquals(80.00, purchase.get().getMontoTotal());
    }

    @Test
    void testFindByCodigoCompra_NonExistingPurchase() {
        Optional<CompletedPurchase> purchase = purchaseRepository.findByCodigoCompra("COMP-999");
        
        assertFalse(purchase.isPresent());
    }

    @Test
    void testFindByCodigoCliente() {
        List<CompletedPurchase> purchases = purchaseRepository.findByCodigoCliente("CLI-001");
        
        assertNotNull(purchases);
        assertFalse(purchases.isEmpty());
        
        for (CompletedPurchase purchase : purchases) {
            assertEquals("CLI-001", purchase.getCodigoCliente());
        }
    }

    @Test
    void testPurchaseHasProducts() {
        Optional<CompletedPurchase> purchase = purchaseRepository.findByCodigoCompra("COMP-001");
        
        assertTrue(purchase.isPresent());
        assertNotNull(purchase.get().getProductos());
        assertEquals(3, purchase.get().getProductos().size());
        
        assertEquals("P-001", purchase.get().getProductos().get(0).getCodigoProducto());
        assertEquals(100, purchase.get().getProductos().get(0).getCantidad());
        assertEquals(0.45, purchase.get().getProductos().get(0).getPrecioUnitario());
        assertEquals(45.00, purchase.get().getProductos().get(0).getSubtotal());
    }
}
