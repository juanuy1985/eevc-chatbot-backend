package com.example.demo.controller;

import com.example.demo.model.ChatRequest;
import com.example.demo.model.ChatResponse;
import com.example.demo.model.Client;
import com.example.demo.service.AiService;
import com.example.demo.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    private final AiService aiService;
    private final ClientService clientService;

    public AiController(AiService aiService, ClientService clientService) {
        this.aiService = aiService;
        this.clientService = clientService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (request.getCodigoCliente() == null || request.getCodigoCliente().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Validate client existence
        Optional<Client> clientOpt = clientService.getClientByCodigoCliente(request.getCodigoCliente());
        if (clientOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Client client = clientOpt.get();
        ChatResponse response = aiService.chat(request.getMessage(), client);
        return ResponseEntity.ok(response);
    }
}
