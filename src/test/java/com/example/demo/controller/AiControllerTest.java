package com.example.demo.controller;

import com.example.demo.model.ChatResponse;
import com.example.demo.model.Client;
import com.example.demo.service.AiService;
import com.example.demo.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiController.class)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiService aiService;

    @MockBean
    private ClientService clientService;

    @Test
    void testChat_WithValidMessage() throws Exception {
        // Arrange
        Client client = new Client("CLI-001", "Juan Carlos", "Av. Test", "District", "Lima", "123456", "test@email.com");
        Map<String, Object> information = new HashMap<>();
        information.put("type", "request_info");
        information.put("response", List.of("perno", "tuerca"));

        ChatResponse chatResponse = new ChatResponse(
                "CLI-001",
                "I'm retrieving the information about perno and tuerca for you.",
                information
        );

        when(clientService.getClientByCodigoCliente("CLI-001")).thenReturn(Optional.of(client));
        when(aiService.chat(anyString(), any(Client.class))).thenReturn(chatResponse);

        String requestBody = "{\"message\": \"What is the price of perno and tuerca?\", \"codigoCliente\": \"CLI-001\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client").value("CLI-001"))
                .andExpect(jsonPath("$.responseMessage").value("I'm retrieving the information about perno and tuerca for you."))
                .andExpect(jsonPath("$.information.type").value("request_info"));
    }

    @Test
    void testChat_WithProductQuery() throws Exception {
        // Arrange
        Client client = new Client("CLI-001", "Juan Carlos", "Av. Test", "District", "Lima", "123456", "test@email.com");
        Map<String, Object> information = new HashMap<>();
        information.put("type", "request_info");
        information.put("response", List.of("perno", "tuerca", "volanda"));

        ChatResponse chatResponse = new ChatResponse(
                "CLI-001",
                "Let me get you the stock information for perno, tuerca, and volanda.",
                information
        );

        when(clientService.getClientByCodigoCliente("CLI-001")).thenReturn(Optional.of(client));
        when(aiService.chat(anyString(), any(Client.class))).thenReturn(chatResponse);

        String requestBody = "{\"message\": \"What products do you have?\", \"codigoCliente\": \"CLI-001\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client").value("CLI-001"))
                .andExpect(jsonPath("$.responseMessage").value("Let me get you the stock information for perno, tuerca, and volanda."))
                .andExpect(jsonPath("$.information.type").value("request_info"));
    }

    @Test
    void testChat_WithEmptyMessage() throws Exception {
        // Arrange
        String requestBody = "{\"message\": \"\", \"codigoCliente\": \"CLI-001\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChat_WithNullMessage() throws Exception {
        // Arrange
        String requestBody = "{\"message\": null, \"codigoCliente\": \"CLI-001\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChat_WithNonExistentClient() throws Exception {
        // Arrange
        when(clientService.getClientByCodigoCliente("CLI-999")).thenReturn(Optional.empty());

        String requestBody = "{\"message\": \"Hello\", \"codigoCliente\": \"CLI-999\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void testChat_WithMissingCodigoCliente() throws Exception {
        // Arrange
        String requestBody = "{\"message\": \"Hello\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChat_WithPurchaseRequest() throws Exception {
        // Arrange
        Client client = new Client("CLI-001", "Juan Carlos", "Av. Test", "District", "Lima", "123456", "test@email.com");
        Map<String, Object> information = new HashMap<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "perno");
        item1.put("quantity", 10);
        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "tuerca");
        item2.put("quantity", 5);
        information.put("type", "purchase");
        information.put("response", List.of(item1, item2));

        ChatResponse chatResponse = new ChatResponse(
                "CLI-001",
                "I'm processing your purchase order for 10 pernos and 5 tuercas.",
                information
        );

        when(clientService.getClientByCodigoCliente("CLI-001")).thenReturn(Optional.of(client));
        when(aiService.chat(anyString(), any(Client.class))).thenReturn(chatResponse);

        String requestBody = "{\"message\": \"I want to buy 10 pernos and 5 tuercas\", \"codigoCliente\": \"CLI-001\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client").value("CLI-001"))
                .andExpect(jsonPath("$.responseMessage").value("I'm processing your purchase order for 10 pernos and 5 tuercas."))
                .andExpect(jsonPath("$.information.type").value("purchase"));
    }
}
