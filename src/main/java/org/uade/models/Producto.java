package org.uade.models;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonProperty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Producto {
    public static int contadorId = 0;

    @BsonProperty
    private int idProducto;
    private String descripcion;
    private float precio;
    private float descuento;
    private float impuestoIVA;
    private String imagen;
}
