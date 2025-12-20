package com.example.demo.model;

public class ChatRequest {
    private String message;
    private String codigoCliente;

    public ChatRequest() {
    }

    public ChatRequest(String message, String codigoCliente) {
        this.message = message;
        this.codigoCliente = codigoCliente;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }
}
