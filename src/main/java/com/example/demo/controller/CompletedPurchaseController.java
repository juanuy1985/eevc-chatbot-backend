package com.example.demo.controller;

import com.example.demo.model.CompletedPurchase;
import com.example.demo.service.CompletedPurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/purchases")
public class CompletedPurchaseController {
    private final CompletedPurchaseService purchaseService;

    public CompletedPurchaseController(CompletedPurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public ResponseEntity<List<CompletedPurchase>> getAllPurchases() {
        List<CompletedPurchase> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{codigoCompra}")
    public ResponseEntity<CompletedPurchase> getPurchaseByCodigoCompra(@PathVariable String codigoCompra) {
        return purchaseService.getPurchaseByCodigoCompra(codigoCompra)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{codigoCliente}")
    public ResponseEntity<List<CompletedPurchase>> getPurchasesByCodigoCliente(@PathVariable String codigoCliente) {
        List<CompletedPurchase> purchases = purchaseService.getPurchasesByCodigoCliente(codigoCliente);
        return ResponseEntity.ok(purchases);
    }
}
