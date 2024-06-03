package org.uade.models;

import java.util.List;

public class Usuario {

    private int id_usuario;
    private String nombre;
    private String apellido;
    private String documento;
    private String direcion;
    private List<Pedido> HistorialPedidos;
    private Float cuentaCorriente;

    private PedidoConfirmado pedidoConfirmado;
    private List<Carrito> carritosDisponibles;

    public Usuario() {
    }

    public Usuario(int id_usuario, String nombre, String apellido, String documento, String direcion, List<Pedido> historialPedidos, Float cuentaCorriente) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.direcion = direcion;
        HistorialPedidos = historialPedidos;
        this.cuentaCorriente = cuentaCorriente;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public String getDirecion() {
        return direcion;
    }

    public List<Pedido> getHistorialPedidos() {
        return HistorialPedidos;
    }

    public Float getCuentaCorriente() {
        return cuentaCorriente;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setDirecion(String direcion) {
        this.direcion = direcion;
    }

    public void setHistorialPedidos(List<Pedido> historialPedidos) {
        HistorialPedidos = historialPedidos;
    }

    public void setCuentaCorriente(Float cuentaCorriente) {
        this.cuentaCorriente = cuentaCorriente;
    }

    public void agregarCarrito(Carrito carrito) {
        carritosDisponibles.add(carrito);
    }

    public void generarPedido(int id_carrito) {
        Carrito carrito = obtenerCarritoPorId(id_carrito);
        if (carrito != null) {
            Pedido nuevoPedido = new Pedido(carrito);
            HistorialPedidos.add(nuevoPedido);
            PedidoConfirmado.agregarPedido(nuevoPedido);
            System.out.println("Pedido generado y agregado al historial y al gestor de pedidos.");
        } else {
            System.out.println("No se pudo encontrar el carrito.");
        }
    }

    private Carrito obtenerCarritoPorId(int id_carrito) {
        for (Carrito carrito : carritosDisponibles) {
            if (carrito.getId_carrito() == id_carrito) {
                return carrito;
            }
        }
        return null;
    }

}
