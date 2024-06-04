package org.uade.models;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Producto {
    private int id_articulo;
    private String descripcion;
    private Double precio;
    //private imagen
    private String comentarios;
}
