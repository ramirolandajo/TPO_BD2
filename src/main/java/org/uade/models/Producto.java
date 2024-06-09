package org.uade.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonIgnore;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Producto {
    private int idProducto;
    private String descripcion;
    private float precio;
    private float descuento;
    private float impuestoIVA;
    private String imagen;
}
