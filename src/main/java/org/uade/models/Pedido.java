package org.uade.models;

import java.util.List;

public class Pedido {

    private int id_pedido;
    private List<Producto> productos;
    private String estado;

    public Pedido() {
    }


    public Pedido(int id_pedido, List<Producto> productos, String estado) {
        this.id_pedido = id_pedido;
        this.productos = productos;
        this.estado = estado;
    }

    public Pedido(Carrito carrito) {
    }


    public int getId_pedido() {
        return id_pedido;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public String getEstado() {
        return estado;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}


