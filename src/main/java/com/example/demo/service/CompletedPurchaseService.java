package com.example.demo.service;

import com.example.demo.model.CompletedPurchase;
import com.example.demo.repository.CompletedPurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompletedPurchaseService {
    private final CompletedPurchaseRepository purchaseRepository;

    public CompletedPurchaseService(CompletedPurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public List<CompletedPurchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public Optional<CompletedPurchase> getPurchaseByCodigoCompra(String codigoCompra) {
        return purchaseRepository.findByCodigoCompra(codigoCompra);
    }

    public List<CompletedPurchase> getPurchasesByCodigoCliente(String codigoCliente) {
        return purchaseRepository.findByCodigoCliente(codigoCliente);
    }
}
