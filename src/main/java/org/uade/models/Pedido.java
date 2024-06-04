package org.uade.models;

import java.util.List;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pedido {

    private int idPedido;
    private List<Producto> productos;
    private String estado;
    private int idUsuario;
}


