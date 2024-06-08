package org.uade.models;

import java.util.List;
import java.util.Map;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pedido {
    private int idPedido;
    private Map<Producto, Integer> productos;
    private Usuario usuario;
    private Double precioTotal;
}


