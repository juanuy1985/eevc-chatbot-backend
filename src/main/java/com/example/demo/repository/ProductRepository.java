package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private final List<Product> products = new ArrayList<>();

    public ProductRepository() {
        // Initialize with sample data
        products.add(new Product("001", "perno", "Perno Hexagonal 1/4 * 4", 1000));
        products.add(new Product("002", "perno", "Perno Hexagonal 1/4 * 5", 800));
        products.add(new Product("003", "tuerca", "Tuerca Hexagonal 1/4", 1500));
        products.add(new Product("004", "perno", "Perno Hexagonal 3/8 * 6", 600));
        products.add(new Product("005", "arandela", "Arandela Plana 1/4", 2000));
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
