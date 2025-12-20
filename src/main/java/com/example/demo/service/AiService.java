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
import java.util.stream.Collectors;

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

        // STEP 1: Load ALL product information from the database
        List<Product> allProducts = productRepository.findAll();
        String productContext = buildDetailedProductContext(allProducts);

        // Build the system message with complete product information
        String systemMessage = "You are a helpful assistant for an e-commerce store that sells three types of products: perno, tuerca, and volanda. " +
                "You have access to the complete product catalog below.\n\n" +
                "Analyze the user's request and filter the products that match what the user is asking for.\n\n" +
                "For requests about prices or stock information, respond with:\n" +
                "{\n" +
                "  \"requestType\": \"request_info\",\n" +
                "  \"productCodes\": [\"P-001\", \"P-002\", \"V-005\", ...],\n" +
                "  \"message\": \"Estoy recuperando la información de precios y stock\"\n" +
                "}\n\n" +
                "IMPORTANT: In productCodes, include ONLY the product codes (codigoProducto) of products that match the user's request. " +
                "Analyze the user's message carefully and match against product names, types, sizes, and specifications.\n" +
                "For example:\n" +
                "- User asks for 'Perno Hexagonal 1/4x2' → return [\"P-001\"] if that product exists\n" +
                "- User asks for 'volandas Planas M8' → return codes of all M8 flat volandas\n" +
                "- User asks for multiple items → return codes of ALL matching products\n\n" +
                "For purchase requests, respond with:\n" +
                "{\n" +
                "  \"requestType\": \"purchase\",\n" +
                "  \"items\": [{\"name\": \"product1\", \"quantity\": 10}, {\"name\": \"product2\", \"quantity\": 5}, ...],\n" +
                "  \"message\": \"A natural message saying you're processing the purchase\"\n" +
                "}\n\n" +
                "Complete Product Catalog:\n" + productContext;

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
                .maxTokens(1000)
                .temperature(0.3)
                .build();

        // Call OpenAI API
        var chatCompletion = openAiService.createChatCompletion(chatCompletionRequest);
        
        if (chatCompletion.getChoices() == null || chatCompletion.getChoices().isEmpty()) {
            throw new RuntimeException("OpenAI API returned no response choices");
        }
        
        String aiResponse = chatCompletion.getChoices().get(0).getMessage().getContent();

        // Parse AI response and build structured response with filtered products
        return parseAiResponse(aiResponse, client.getCodigoCliente(), allProducts);
    }

    private ChatResponse parseAiResponse(String aiResponse, String codigoCliente, List<Product> allProducts) {
        try {
            // Try to parse as JSON
            JsonNode jsonNode = objectMapper.readTree(aiResponse);
            
            String requestType = jsonNode.has("requestType") ? jsonNode.get("requestType").asText() : "unknown";
            String message = jsonNode.has("message") ? jsonNode.get("message").asText() : "Processing your request";
            
            Map<String, Object> information = new HashMap<>();
            
            if ("request_info".equals(requestType)) {
                // Request for prices or stock - filter products by codes returned from AI
                information.put("type", "request_info");
                List<Product> productDetails = new ArrayList<>();
                if (jsonNode.has("productCodes") && jsonNode.get("productCodes").isArray()) {
                    List<String> productCodes = new ArrayList<>();
                    jsonNode.get("productCodes").forEach(node -> {
                        productCodes.add(node.asText());
                    });
                    // Filter products by the codes returned from AI
                    if (!productCodes.isEmpty()) {
                        productDetails = allProducts.stream()
                                .filter(product -> productCodes.contains(product.getCodigoProducto()))
                                .collect(Collectors.toList());
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

    /**
     * Build detailed product context with complete information for AI filtering
     */
    private String buildDetailedProductContext(List<Product> products) {
        StringBuilder context = new StringBuilder();
        
        for (Product product : products) {
            context.append(String.format(
                "Code: %s, Type: %s, Name: %s, Stock: %d, Unit Price: %.2f, Wholesale Price: %.2f\n",
                product.getCodigoProducto(),
                product.getTipoProducto(),
                product.getNombreProducto(),
                product.getCantidadStock(),
                product.getPrecioUnitario(),
                product.getPrecioXMayor()
            ));
        }

        return context.toString();
    }
}
