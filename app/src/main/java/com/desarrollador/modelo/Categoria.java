package com.desarrollador.modelo;

public class Categoria {

    private int id;
    private String name;
    private String iconUrl;

    public Categoria(int id, String name, String iconUrl) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
