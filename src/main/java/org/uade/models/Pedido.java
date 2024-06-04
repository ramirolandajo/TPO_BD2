package org.uade.models;

import java.util.List;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pedido {

    private int id_pedido;
    private List<Producto> productos;
    private String estado;

    public Pedido(Carrito carrito) {
    }
}


