package org.uade.models;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Usuario {

    private int idUsuario;
    private String nombreCompleto;
    private String documento;
    private String direccion;
    private List<Pedido> historialPedidos;
    private Float cuentaCorriente;

    private PedidoConfirmado pedidoConfirmado;
    private List<Carrito> carritosDisponibles;


    public void generarPedido(int id_carrito) {
        Carrito carrito = obtenerCarritoPorId(id_carrito);
        if (carrito != null) {
            Pedido nuevoPedido = new Pedido(carrito);
            historialPedidos.add(nuevoPedido);
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
