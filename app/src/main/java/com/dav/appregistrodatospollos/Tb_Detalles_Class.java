package com.dav.appregistrodatospollos;

public class Tb_Detalles_Class {
    String Id_detalle;//idVenta
    String fecha;//producto
    String granja;//fechaVenta
    String galpon; //precio
    String galponero; //sincronizado
    String mortalidad;
    String alimento;
    String peso;
    String veterinario;

    public Tb_Detalles_Class(String id_detalle, String fecha, String granja,
                 String galpon, String galponero, String mortalidad, String alimento, String peso, String veterinario) {
        Id_detalle = id_detalle;
        this.fecha = fecha;
        this.granja = granja;
        this.galpon = galpon;
        this.galponero = galponero;
        this.mortalidad = mortalidad;
        this.alimento = alimento;
        this.peso = peso;
        this.veterinario = veterinario;
    }

    public String getMortalidad() {
        return mortalidad;
    }

    public void setMortalidad(String mortalidad) {
        this.mortalidad = mortalidad;
    }

    public String getAlimento() {
        return alimento;
    }

    public void setAlimento(String alimento) {
        this.alimento = alimento;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }

    public String getId_detalle() {
        return Id_detalle;
    }

    public void setId_detalle(String id_detalle) {
        Id_detalle = id_detalle;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getGranja() {
        return granja;
    }

    public void setGranja(String granja) {
        this.granja = granja;
    }

    public String getGalpon() {
        return galpon;
    }

    public void setGalpon(String galpon) {
        this.galpon = galpon;
    }

    public String getGalponero() {
        return galponero;
    }

    public void setGalponero(String galponero) {
        this.galponero = galponero;
    }
}
