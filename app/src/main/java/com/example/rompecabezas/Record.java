package com.example.rompecabezas;

public class Record {
    public int id;
    public String imagenUri;
    public String fecha;
    public long tiempo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getImagenUri() {
        return imagenUri;
    }
    public long getTiempo() {
        return tiempo;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public String getFecha() {
        return fecha;
    }
    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }
    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }
    public Record(int id, long tiempo, String imagenUri, String fecha) {
        this.id = id;
        this.tiempo = tiempo;
        this.imagenUri = imagenUri;
        this.fecha = fecha;
    }
}