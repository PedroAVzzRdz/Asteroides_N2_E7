package com.example.asteroidesreal;

public class Score {
    private long id;
    private int puntaje;
    private long fecha;
    private String nombre;

    public Score() {
    }

    public Score(int puntaje, long fecha, String nombre) {
        this.puntaje = puntaje;
        this.fecha = fecha;
        this.nombre = nombre;
    }

    public Score(long id, int puntaje, long fecha, String nombre) {
        this.id = id;
        this.puntaje = puntaje;
        this.fecha = fecha;
        this.nombre = nombre;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

