package com.example.demo.controller;

import com.example.demo.model.Client;
import com.example.demo.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{codigoCliente}")
    public ResponseEntity<Client> getClientByCodigoCliente(@PathVariable String codigoCliente) {
        return clientService.getClientByCodigoCliente(codigoCliente)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/distrito/{distrito}")
    public ResponseEntity<List<Client>> getClientsByDistrito(@PathVariable String distrito) {
        List<Client> clients = clientService.getClientsByDistrito(distrito);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<Client>> getClientsByCiudad(@PathVariable String ciudad) {
        List<Client> clients = clientService.getClientsByCiudad(ciudad);
        return ResponseEntity.ok(clients);
    }
}
