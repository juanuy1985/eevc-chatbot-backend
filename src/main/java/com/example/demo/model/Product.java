package com.example.demo.model;

public class Product {
    private String codigoProducto;
    private String tipoProducto;
    private String nombreProducto;
    private int cantidadStock;

    public Product() {
    }

    public Product(String codigoProducto, String tipoProducto, String nombreProducto, int cantidadStock) {
        this.codigoProducto = codigoProducto;
        this.tipoProducto = tipoProducto;
        this.nombreProducto = nombreProducto;
        this.cantidadStock = cantidadStock;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(int cantidadStock) {
        this.cantidadStock = cantidadStock;
    }
}
