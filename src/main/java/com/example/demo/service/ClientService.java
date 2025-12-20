package com.example.demo.service;

import com.example.demo.model.Client;
import com.example.demo.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientByCodigoCliente(String codigoCliente) {
        return clientRepository.findByCodigoCliente(codigoCliente);
    }

    public List<Client> getClientsByDistrito(String distrito) {
        return clientRepository.findByDistrito(distrito);
    }

    public List<Client> getClientsByCiudad(String ciudad) {
        return clientRepository.findByCiudad(ciudad);
    }
}
