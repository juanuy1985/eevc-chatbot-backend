package com.example.demo.repository;

import com.example.demo.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void testFindAll() {
        List<Client> clients = clientRepository.findAll();
        
        assertNotNull(clients);
        assertFalse(clients.isEmpty());
        assertTrue(clients.size() >= 50);
    }

    @Test
    void testFindByCodigoCliente_ExistingClient() {
        Optional<Client> client = clientRepository.findByCodigoCliente("CLI-001");
        
        assertTrue(client.isPresent());
        assertEquals("CLI-001", client.get().getCodigoCliente());
        assertEquals("Juan Carlos Rodríguez Pérez", client.get().getNombreCompleto());
        assertEquals("Av. Javier Prado Este 456", client.get().getDireccion());
        assertEquals("San Isidro", client.get().getDistrito());
        assertEquals("Lima", client.get().getCiudad());
        assertEquals("+51 1 2345678", client.get().getTelefono());
        assertEquals("jrodriguez@email.com", client.get().getEmail());
    }

    @Test
    void testFindByCodigoCliente_NonExistingClient() {
        Optional<Client> client = clientRepository.findByCodigoCliente("CLI-999");
        
        assertFalse(client.isPresent());
    }

    @Test
    void testFindByDistrito() {
        List<Client> clients = clientRepository.findByDistrito("Miraflores");
        
        assertNotNull(clients);
        assertFalse(clients.isEmpty());
        
        for (Client client : clients) {
            assertEquals("Miraflores", client.getDistrito());
        }
    }

    @Test
    void testFindByCiudad() {
        List<Client> clients = clientRepository.findByCiudad("Lima");
        
        assertNotNull(clients);
        assertFalse(clients.isEmpty());
        
        for (Client client : clients) {
            assertEquals("Lima", client.getCiudad());
        }
    }
}
