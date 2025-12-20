package com.example.demo.repository;

import com.example.demo.model.Client;
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
public class ClientRepository {
    private final List<Client> clients = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClientRepository() {
        loadClientsFromJson();
    }

    private void loadClientsFromJson() {
        try {
            ClassPathResource resource = new ClassPathResource("database/clients.json");
            try (InputStream inputStream = resource.getInputStream()) {
                List<Client> loadedClients = objectMapper.readValue(inputStream, new TypeReference<List<Client>>() {});
                clients.addAll(loadedClients);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load clients from database/clients.json", e);
        }
    }

    public List<Client> findAll() {
        return new ArrayList<>(clients);
    }

    public Optional<Client> findByCodigoCliente(String codigoCliente) {
        return clients.stream()
                .filter(client -> client.getCodigoCliente().equalsIgnoreCase(codigoCliente))
                .findFirst();
    }

    public List<Client> findByDistrito(String distrito) {
        return clients.stream()
                .filter(client -> client.getDistrito().equalsIgnoreCase(distrito))
                .toList();
    }

    public List<Client> findByCiudad(String ciudad) {
        return clients.stream()
                .filter(client -> client.getCiudad().equalsIgnoreCase(ciudad))
                .toList();
    }
}
