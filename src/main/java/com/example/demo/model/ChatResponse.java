package com.example.demo.model;

import java.util.Map;

public class ChatResponse {
    private String client;
    private String responseMessage;
    private Map<String, Object> information;

    public ChatResponse() {
    }

    public ChatResponse(String client, String responseMessage, Map<String, Object> information) {
        this.client = client;
        this.responseMessage = responseMessage;
        this.information = information;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Map<String, Object> getInformation() {
        return information;
    }

    public void setInformation(Map<String, Object> information) {
        this.information = information;
    }
}
