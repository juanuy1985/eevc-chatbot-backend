package com.example.demo.controller;

import com.example.demo.model.CompletedPurchase;
import com.example.demo.model.PurchaseItem;
import com.example.demo.service.CompletedPurchaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompletedPurchaseController.class)
class CompletedPurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompletedPurchaseService purchaseService;

    @Test
    void testGetAllPurchases() throws Exception {
        List<PurchaseItem> items = Arrays.asList(
                new PurchaseItem("P-001", "Perno Hexagonal", 100, 0.45, 45.00)
        );
        List<CompletedPurchase> purchases = Arrays.asList(
                new CompletedPurchase("COMP-001", "CLI-001", "Juan Carlos Rodríguez Pérez", "2024-01-15", items, 80.00),
                new CompletedPurchase("COMP-002", "CLI-002", "María Isabel García Torres", "2024-01-18", items, 53.50)
        );
        when(purchaseService.getAllPurchases()).thenReturn(purchases);

        mockMvc.perform(get("/purchases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].codigoCompra").value("COMP-001"))
                .andExpect(jsonPath("$[1].codigoCompra").value("COMP-002"));
    }

    @Test
    void testGetPurchaseByCodigoCompra_ExistingPurchase() throws Exception {
        List<PurchaseItem> items = Arrays.asList(
                new PurchaseItem("P-001", "Perno Hexagonal", 100, 0.45, 45.00)
        );
        CompletedPurchase purchase = new CompletedPurchase("COMP-001", "CLI-001", "Juan Carlos Rodríguez Pérez", "2024-01-15", items, 80.00);
        when(purchaseService.getPurchaseByCodigoCompra("COMP-001")).thenReturn(Optional.of(purchase));

        mockMvc.perform(get("/purchases/COMP-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoCompra").value("COMP-001"))
                .andExpect(jsonPath("$.codigoCliente").value("CLI-001"))
                .andExpect(jsonPath("$.nombreCliente").value("Juan Carlos Rodríguez Pérez"))
                .andExpect(jsonPath("$.fecha").value("2024-01-15"))
                .andExpect(jsonPath("$.montoTotal").value(80.00));
    }

    @Test
    void testGetPurchaseByCodigoCompra_NonExistingPurchase() throws Exception {
        when(purchaseService.getPurchaseByCodigoCompra("COMP-999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/purchases/COMP-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPurchasesByCodigoCliente() throws Exception {
        List<PurchaseItem> items = Arrays.asList(
                new PurchaseItem("P-001", "Perno Hexagonal", 100, 0.45, 45.00)
        );
        List<CompletedPurchase> purchases = Arrays.asList(
                new CompletedPurchase("COMP-001", "CLI-001", "Juan Carlos Rodríguez Pérez", "2024-01-15", items, 80.00)
        );
        when(purchaseService.getPurchasesByCodigoCliente("CLI-001")).thenReturn(purchases);

        mockMvc.perform(get("/purchases/client/CLI-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].codigoCliente").value("CLI-001"));
    }
}
