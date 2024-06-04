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
    private String imagen;
    private String comentarios;
}
