package com.dam.jcalzado.todoapp.Modelos;

public class Tarea {

    private int id;
    private String titulo;
    private String prioridad ;
    private String estado;
    private String deadline;

    public Tarea() {
    }

    public Tarea(int id, String titulo, String prioridad, String estado, String deadline) {
        this.id = id;
        this.titulo = titulo;
        this.prioridad = prioridad;
        this.estado = estado;
        this.deadline = deadline;
    }

    public int getId() { return id; }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
