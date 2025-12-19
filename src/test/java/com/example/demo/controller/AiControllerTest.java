package com.example.demo.controller;

import com.example.demo.model.ChatResponse;
import com.example.demo.service.AiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

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

    @Test
    void testChat_WithValidMessage() throws Exception {
        // Arrange
        Map<String, Object> information = new HashMap<>();
        information.put("model", "gpt-3.5-turbo");
        information.put("productsAvailable", 10);
        information.put("timestamp", 1234567890L);

        ChatResponse chatResponse = new ChatResponse(
                "eevc-chatbot",
                "Hello! How can I help you today?",
                information
        );

        when(aiService.chat(anyString())).thenReturn(chatResponse);

        String requestBody = "{\"message\": \"Hello\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client").value("eevc-chatbot"))
                .andExpect(jsonPath("$.responseMessage").value("Hello! How can I help you today?"))
                .andExpect(jsonPath("$.information.model").value("gpt-3.5-turbo"))
                .andExpect(jsonPath("$.information.productsAvailable").value(10))
                .andExpect(jsonPath("$.information.timestamp").value(1234567890L));
    }

    @Test
    void testChat_WithProductQuery() throws Exception {
        // Arrange
        Map<String, Object> information = new HashMap<>();
        information.put("model", "gpt-3.5-turbo");
        information.put("productsAvailable", 25);
        information.put("timestamp", 1234567890L);

        ChatResponse chatResponse = new ChatResponse(
                "eevc-chatbot",
                "We have various types of products including pernos, tuercas, and volandas.",
                information
        );

        when(aiService.chat(anyString())).thenReturn(chatResponse);

        String requestBody = "{\"message\": \"What products do you have?\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client").value("eevc-chatbot"))
                .andExpect(jsonPath("$.responseMessage").value("We have various types of products including pernos, tuercas, and volandas."))
                .andExpect(jsonPath("$.information.model").value("gpt-3.5-turbo"))
                .andExpect(jsonPath("$.information.productsAvailable").value(25));
    }

    @Test
    void testChat_WithEmptyMessage() throws Exception {
        // Arrange
        String requestBody = "{\"message\": \"\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChat_WithNullMessage() throws Exception {
        // Arrange
        String requestBody = "{\"message\": null}";

        // Act & Assert
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
