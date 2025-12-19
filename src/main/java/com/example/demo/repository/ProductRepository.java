package com.example.demo.repository;

import com.example.demo.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private final List<Product> products = new ArrayList<>();
    private final Map<String, String> typeToFileMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductRepository() {
        // Map product types to their corresponding JSON files
        typeToFileMap.put("perno", "database/pernos.json");
        typeToFileMap.put("tuerca", "database/tuercas.json");
        typeToFileMap.put("volanda", "database/volandas.json");

        // Load products from all JSON files
        loadProductsFromJsonFiles();
    }

    private void loadProductsFromJsonFiles() {
        for (String jsonFile : typeToFileMap.values()) {
            try {
                ClassPathResource resource = new ClassPathResource(jsonFile);
                InputStream inputStream = resource.getInputStream();
                List<Product> loadedProducts = objectMapper.readValue(inputStream, new TypeReference<List<Product>>() {});
                products.addAll(loadedProducts);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load products from " + jsonFile, e);
            }
        }
    }

    public List<Product> findByTipoProducto(String tipoProducto) {
        return products.stream()
                .filter(product -> product.getTipoProducto().equalsIgnoreCase(tipoProducto))
                .collect(Collectors.toList());
    }

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }
}
