package com.example.demo.service;

import com.example.demo.model.ChatResponse;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {
    private final OpenAiService openAiService;
    private final ProductRepository productRepository;

    public AiService(
            @Value("${openai.api.key}") String apiKey,
            ProductRepository productRepository) {
        this.openAiService = new OpenAiService(apiKey);
        this.productRepository = productRepository;
    }

    public ChatResponse chat(String userMessage) {
        // Get product context from database
        List<Product> allProducts = productRepository.findAll();
        String productContext = buildProductContext(allProducts);

        // Build the system message with product context
        String systemMessage = "You are a helpful assistant for an e-commerce store. " +
                "Use the following product information to help customers: " + productContext;

        // Create chat messages
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", systemMessage));
        messages.add(new ChatMessage("user", userMessage));

        // Create chat completion request
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .maxTokens(500)
                .temperature(0.7)
                .build();

        // Call OpenAI API
        String aiResponse = openAiService.createChatCompletion(chatCompletionRequest)
                .getChoices().get(0).getMessage().getContent();

        // Build information map with context
        Map<String, Object> information = new HashMap<>();
        information.put("model", "gpt-3.5-turbo");
        information.put("productsAvailable", allProducts.size());
        information.put("timestamp", System.currentTimeMillis());

        // Return formatted response
        return new ChatResponse("eevc-chatbot", aiResponse, information);
    }

    private String buildProductContext(List<Product> products) {
        StringBuilder context = new StringBuilder();
        context.append("Products available: ");
        
        // Group products by type
        Map<String, List<Product>> productsByType = new HashMap<>();
        for (Product product : products) {
            productsByType.computeIfAbsent(product.getTipoProducto(), k -> new ArrayList<>()).add(product);
        }

        for (Map.Entry<String, List<Product>> entry : productsByType.entrySet()) {
            context.append(entry.getKey()).append(" (").append(entry.getValue().size()).append(" items), ");
        }

        return context.toString();
    }
}
