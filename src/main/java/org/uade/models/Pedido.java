package org.uade.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pedido {
    private int idPedido;
    private Map<String, Integer> productos;
    private Usuario usuario;
    private float precioTotal;
}


