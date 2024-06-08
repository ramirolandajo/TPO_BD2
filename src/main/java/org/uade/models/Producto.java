package org.uade.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Producto {
    public static int contadorId = 0;

    private int idProducto;
    private String descripcion;
    private float precio;
    private float descuento;
    private float impuestoIVA;
    private String imagen;
}
