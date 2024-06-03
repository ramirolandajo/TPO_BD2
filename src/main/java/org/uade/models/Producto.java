package org.uade.models;

public class Producto {

    private int id_articulo;
    private String descripcion;
    //private imagen
    private String comentarios;

    public Producto() {
    }

    public Producto(int id_articulo, String descripcion, String comentarios) {
        this.id_articulo = id_articulo;
        this.descripcion = descripcion;
        this.comentarios = comentarios;
    }

    public int getId_articulo() {
        return id_articulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setId_articulo(int id_articulo) {
        this.id_articulo = id_articulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
}
