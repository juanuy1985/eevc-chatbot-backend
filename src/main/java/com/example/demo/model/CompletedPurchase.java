package com.example.demo.model;

import java.util.List;

public class CompletedPurchase {
    private String codigoCompra;
    private String codigoCliente;
    private String nombreCliente;
    private String fecha;
    private List<PurchaseItem> productos;
    private double montoTotal;

    public CompletedPurchase() {
    }

    public CompletedPurchase(String codigoCompra, String codigoCliente, String nombreCliente, String fecha, List<PurchaseItem> productos, double montoTotal) {
        this.codigoCompra = codigoCompra;
        this.codigoCliente = codigoCliente;
        this.nombreCliente = nombreCliente;
        this.fecha = fecha;
        this.productos = productos;
        this.montoTotal = montoTotal;
    }

    public String getCodigoCompra() {
        return codigoCompra;
    }

    public void setCodigoCompra(String codigoCompra) {
        this.codigoCompra = codigoCompra;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<PurchaseItem> getProductos() {
        return productos;
    }

    public void setProductos(List<PurchaseItem> productos) {
        this.productos = productos;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }
}
