package com.example.diana;

public class Usuario {

    private String nombre;
    private String apellidos;
    private String alias;
    private String email;
    private int puntuacion;

    private Roles rol;



    public Usuario(String nombre, String apellidos, String alias, String email) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.alias = alias;
        this.email = email;
    }

    public Usuario() {
    }

    public Usuario(String alias, int puntuacion) {
        this.alias = alias;
        this.puntuacion = puntuacion;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }
}
