package com.example.demo.service;

import com.example.demo.model.ChatResponse;
import com.example.demo.model.Client;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    public AiService(
            @Value("${openai.api.key}") String apiKey,
            ProductRepository productRepository) {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("your-api-key-here")) {
            throw new IllegalArgumentException("OpenAI API key must be configured. Set OPENAI_API_KEY environment variable.");
        }
        this.openAiService = new OpenAiService(apiKey);
        this.productRepository = productRepository;
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponse chat(String userMessage, Client client) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("User message cannot be null or empty");
        }
        
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }

        // Build client context
        String clientContext = String.format(
            "Client Information: Code: %s, Name: %s, Address: %s, District: %s, City: %s, Phone: %s, Email: %s",
            client.getCodigoCliente(),
            client.getNombreCompleto(),
            client.getDireccion(),
            client.getDistrito(),
            client.getCiudad(),
            client.getTelefono(),
            client.getEmail()
        );

        // Get product context from database
        List<Product> allProducts = productRepository.findAll();
        String productContext = buildProductContext(allProducts);

        // Build the system message with product and client context
        String systemMessage = "You are a helpful assistant for an e-commerce store that sells three types of products: perno, tuerca, and volanda. " +
                "Analyze the user's request and respond in JSON format.\n\n" +
                "For requests about prices or stock information, respond with:\n" +
                "{\n" +
                "  \"requestType\": \"request_info\",\n" +
                "  \"productKeywords\": [\"keyword1\", \"keyword2\", ...],\n" +
                "  \"message\": \"A natural message saying you're retrieving the information\"\n" +
                "}\n\n" +
                "IMPORTANT: In productKeywords, extract ALL relevant identifying terms from the user's request. Be comprehensive and include:\n" +
                "- Size specifications (e.g., '1/4x2', '1/4 x 2', 'M8', '3/8')\n" +
                "- Product types (e.g., 'hexagonal', 'plana', 'presion', 'presión')\n" +
                "- Material types when mentioned\n" +
                "For example:\n" +
                "- 'Perno Hexagonal 1/4x2' → extract ['hexagonal', '1/4', 'x 2', '1/4x2']\n" +
                "- 'Volanda Plana M8' → extract ['plana', 'M8', 'm8']\n" +
                "- 'volanda de presion 3/8' → extract ['presion', 'presión', '3/8', '3 / 8']\n" +
                "Include multiple variations to maximize matching success.\n\n" +
                "For purchase requests, respond with:\n" +
                "{\n" +
                "  \"requestType\": \"purchase\",\n" +
                "  \"items\": [{\"name\": \"product1\", \"quantity\": 10}, {\"name\": \"product2\", \"quantity\": 5}, ...],\n" +
                "  \"message\": \"A natural message saying you're processing the purchase\"\n" +
                "}\n\n" +
                "Product context: " + productContext;

        // Concatenate client context to user message
        String enhancedMessage = clientContext + "\n\nUser request: " + userMessage;

        // Create chat messages
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", systemMessage));
        messages.add(new ChatMessage("user", enhancedMessage));

        // Create chat completion request
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .maxTokens(500)
                .temperature(0.7)
                .build();

        // Call OpenAI API
        var chatCompletion = openAiService.createChatCompletion(chatCompletionRequest);
        
        if (chatCompletion.getChoices() == null || chatCompletion.getChoices().isEmpty()) {
            throw new RuntimeException("OpenAI API returned no response choices");
        }
        
        String aiResponse = chatCompletion.getChoices().get(0).getMessage().getContent();

        // Parse AI response and build structured response
        return parseAiResponse(aiResponse, client.getCodigoCliente());
    }

    private ChatResponse parseAiResponse(String aiResponse, String codigoCliente) {
        try {
            // Try to parse as JSON
            JsonNode jsonNode = objectMapper.readTree(aiResponse);
            
            String requestType = jsonNode.has("requestType") ? jsonNode.get("requestType").asText() : "unknown";
            String message = jsonNode.has("message") ? jsonNode.get("message").asText() : "Processing your request";
            
            Map<String, Object> information = new HashMap<>();
            
            if ("request_info".equals(requestType)) {
                // Request for prices or stock
                information.put("type", "request_info");
                List<Product> productDetails = new ArrayList<>();
                if (jsonNode.has("productKeywords") && jsonNode.get("productKeywords").isArray()) {
                    List<String> keywords = new ArrayList<>();
                    jsonNode.get("productKeywords").forEach(node -> {
                        keywords.add(node.asText());
                    });
                    // Fetch products matching any of the keywords
                    if (!keywords.isEmpty()) {
                        productDetails = productRepository.findByProductNameKeywords(keywords);
                    }
                }
                information.put("response", productDetails);
            } else if ("purchase".equals(requestType)) {
                // Purchase request
                information.put("type", "purchase");
                List<Map<String, Object>> items = new ArrayList<>();
                if (jsonNode.has("items") && jsonNode.get("items").isArray()) {
                    jsonNode.get("items").forEach(node -> {
                        if (node.has("name") && node.has("quantity")) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("name", node.get("name").asText());
                            item.put("quantity", node.get("quantity").asInt());
                            items.add(item);
                        }
                    });
                }
                information.put("response", items);
            } else {
                // Fallback for unexpected format
                information.put("type", "unknown");
                information.put("response", aiResponse);
            }
            
            return new ChatResponse(codigoCliente, message, information);
            
        } catch (JsonProcessingException e) {
            // Log the parsing error for debugging
            System.err.println("Failed to parse AI response as JSON: " + aiResponse);
            System.err.println("Error: " + e.getMessage());
            
            // If not valid JSON, return as plain response
            Map<String, Object> information = new HashMap<>();
            information.put("type", "unknown");
            information.put("response", aiResponse);
            return new ChatResponse(codigoCliente, aiResponse, information);
        }
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
