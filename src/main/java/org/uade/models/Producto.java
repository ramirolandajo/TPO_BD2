package org.uade.models;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Producto {
    private int idProducto;
    private String descripcion;
    private Double precio;
    private Double descuento;
    private Double impuestoIVA;
    private String imagen;
    private String comentarios;
}
