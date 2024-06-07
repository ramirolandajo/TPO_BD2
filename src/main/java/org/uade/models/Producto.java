package org.uade.models;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Producto {
    public static int contadorId;
    private int idProducto;
    private String descripcion;
    private Float precio;
    private Float descuento;
    private Float impuestoIVA;
    private String imagen;
}
