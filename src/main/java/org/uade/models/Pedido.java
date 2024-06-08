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
    public static int contadorId = 0;
    private int idPedido;
    private Map<Producto, Integer> productos;
    private Usuario usuario;
    private float precioTotal;
}


