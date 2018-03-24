package com.desarrollador.modelo;

public class Market {

    private int id;

    private String titulo, calles, descripcion, imagePath;

    private double latitud, longitud;

    private Categoria categoria;

    public Market(int id, String titulo, double latitud, double longitud, String calles,
                  String descripcion, String imagePath, Categoria categoria) {
        this.id = id;
        this.titulo = titulo;
        this.calles = calles;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.imagePath = imagePath;
        this.categoria = categoria;
    }

    public Market(String titulo, double latitud, double longitud, String calles,
                  String descripcion, String imagePath, Categoria categoria) {
        this.titulo = titulo;
        this.calles = calles;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.imagePath = imagePath;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCalles() {
        return calles;
    }

    public void setCalles(String calles) {
        this.calles = calles;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
