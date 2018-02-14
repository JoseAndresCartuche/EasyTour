package com.desarrollador.modelo;

public class Market {

    public String titulo, calles, descripcion;

    public double latitud, longitud;

    public Market(String titulo, double latitud, double longitud, String calles, String descripcion) {
        this.titulo = titulo;
        this.calles = calles;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

}
