package com.example.demo.controller;

import com.example.demo.model.Client;
import com.example.demo.service.ClientService;
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

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Test
    void testGetAllClients() throws Exception {
        List<Client> clients = Arrays.asList(
                new Client("CLI-001", "Juan Carlos Rodríguez Pérez", "Av. Javier Prado Este 456", "San Isidro", "Lima", "+51 1 2345678", "jrodriguez@email.com"),
                new Client("CLI-002", "María Isabel García Torres", "Calle Las Begonias 789", "Miraflores", "Lima", "+51 1 3456789", "mgarcia@email.com")
        );
        when(clientService.getAllClients()).thenReturn(clients);

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].codigoCliente").value("CLI-001"))
                .andExpect(jsonPath("$[0].nombreCompleto").value("Juan Carlos Rodríguez Pérez"))
                .andExpect(jsonPath("$[1].codigoCliente").value("CLI-002"));
    }

    @Test
    void testGetClientByCodigoCliente_ExistingClient() throws Exception {
        Client client = new Client("CLI-001", "Juan Carlos Rodríguez Pérez", "Av. Javier Prado Este 456", "San Isidro", "Lima", "+51 1 2345678", "jrodriguez@email.com");
        when(clientService.getClientByCodigoCliente("CLI-001")).thenReturn(Optional.of(client));

        mockMvc.perform(get("/clients/CLI-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoCliente").value("CLI-001"))
                .andExpect(jsonPath("$.nombreCompleto").value("Juan Carlos Rodríguez Pérez"))
                .andExpect(jsonPath("$.direccion").value("Av. Javier Prado Este 456"))
                .andExpect(jsonPath("$.distrito").value("San Isidro"))
                .andExpect(jsonPath("$.ciudad").value("Lima"));
    }

    @Test
    void testGetClientByCodigoCliente_NonExistingClient() throws Exception {
        when(clientService.getClientByCodigoCliente("CLI-999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/clients/CLI-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetClientsByDistrito() throws Exception {
        List<Client> clients = Arrays.asList(
                new Client("CLI-002", "María Isabel García Torres", "Calle Las Begonias 789", "Miraflores", "Lima", "+51 1 3456789", "mgarcia@email.com")
        );
        when(clientService.getClientsByDistrito("Miraflores")).thenReturn(clients);

        mockMvc.perform(get("/clients/distrito/Miraflores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].distrito").value("Miraflores"));
    }

    @Test
    void testGetClientsByCiudad() throws Exception {
        List<Client> clients = Arrays.asList(
                new Client("CLI-001", "Juan Carlos Rodríguez Pérez", "Av. Javier Prado Este 456", "San Isidro", "Lima", "+51 1 2345678", "jrodriguez@email.com"),
                new Client("CLI-002", "María Isabel García Torres", "Calle Las Begonias 789", "Miraflores", "Lima", "+51 1 3456789", "mgarcia@email.com")
        );
        when(clientService.getClientsByCiudad("Lima")).thenReturn(clients);

        mockMvc.perform(get("/clients/ciudad/Lima"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ciudad").value("Lima"))
                .andExpect(jsonPath("$[1].ciudad").value("Lima"));
    }
}
