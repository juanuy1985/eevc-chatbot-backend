package com.example.demo.model;

public class Client {
    private String codigoCliente;
    private String nombreCompleto;
    private String direccion;
    private String distrito;
    private String ciudad;
    private String telefono;
    private String email;

    public Client() {
    }

    public Client(String codigoCliente, String nombreCompleto, String direccion, String distrito, String ciudad, String telefono, String email) {
        this.codigoCliente = codigoCliente;
        this.nombreCompleto = nombreCompleto;
        this.direccion = direccion;
        this.distrito = distrito;
        this.ciudad = ciudad;
        this.telefono = telefono;
        this.email = email;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
