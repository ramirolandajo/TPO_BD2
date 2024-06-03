package org.uade.models;

import java.util.Date;
import java.util.List;

public class Carrito {

    private int id_carrito;
    private String articulo;
    private int cantidad;
    private Float precio;
    private Float descuento;
    private Date fechaCreacion;
    private boolean carritoCreado;

    private List<Producto> productos;

    public Carrito() {
    }

    public Carrito(int id_carrito, String articulo, int cantidad, Float precio, Float descuento, Date fechaCreacion, boolean carritoCreado) {
        this.id_carrito = id_carrito;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descuento = descuento;
        this.fechaCreacion = fechaCreacion;
        this.carritoCreado = carritoCreado;
    }

    public int getId_carrito() {
        return id_carrito;
    }

    public String getArticulo() {
        return articulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Float getPrecio() {
        return precio;
    }

    public Float getDescuento() {
        return descuento;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public boolean isCarritoCreado() {
        return carritoCreado;
    }

    public void setId_carrito(int id_carrito) {
        this.id_carrito = id_carrito;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public void setDescuento(Float descuento) {
        this.descuento = descuento;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setCarritoCreado(boolean carritoCreado) {
        this.carritoCreado = carritoCreado;
    }


    public Float getSubTotal(Float precio, Float descuento) {
        if (descuento == null) {
            // Por que le tengo q poner la f
            descuento = 0.0f;
        }
        return precio - (precio * descuento / 100);
    }


}
