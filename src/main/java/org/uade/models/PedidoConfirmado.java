package org.uade.models;

import java.util.ArrayList;
import java.util.List;

public class PedidoConfirmado {

    private static List<Pedido> pedidosConfirmados;

    public PedidoConfirmado() {
        this.pedidosConfirmados = new ArrayList<Pedido>();
    }

    public static void agregarPedido(Pedido pedido) {
        pedidosConfirmados.add(pedido);
        System.out.println("Pedido confirmado y agregado a la lista de pedidos.");
    }

    public List<Pedido> getPedidosConfirmados() {
        return pedidosConfirmados;
    }
}