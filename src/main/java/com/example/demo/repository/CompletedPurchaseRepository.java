package com.example.demo.repository;

import com.example.demo.model.CompletedPurchase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CompletedPurchaseRepository {
    private final List<CompletedPurchase> purchases = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompletedPurchaseRepository() {
        loadPurchasesFromJson();
    }

    private void loadPurchasesFromJson() {
        try {
            ClassPathResource resource = new ClassPathResource("database/completed-purchases.json");
            try (InputStream inputStream = resource.getInputStream()) {
                List<CompletedPurchase> loadedPurchases = objectMapper.readValue(inputStream, new TypeReference<List<CompletedPurchase>>() {});
                purchases.addAll(loadedPurchases);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load completed purchases from database/completed-purchases.json", e);
        }
    }

    public List<CompletedPurchase> findAll() {
        return new ArrayList<>(purchases);
    }

    public Optional<CompletedPurchase> findByCodigoCompra(String codigoCompra) {
        return purchases.stream()
                .filter(purchase -> purchase.getCodigoCompra().equalsIgnoreCase(codigoCompra))
                .findFirst();
    }

    public List<CompletedPurchase> findByCodigoCliente(String codigoCliente) {
        return purchases.stream()
                .filter(purchase -> purchase.getCodigoCliente().equalsIgnoreCase(codigoCliente))
                .toList();
    }
}
