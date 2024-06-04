package org.uade.models;

import java.util.List;
import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PedidoConfirmado {

    private static List<Pedido> pedidosConfirmados;

    public static void agregarPedido(Pedido pedido) {
        pedidosConfirmados.add(pedido);
        System.out.println("Pedido confirmado y agregado a la lista de pedidos.");
    }
}