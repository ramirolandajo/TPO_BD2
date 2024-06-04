package org.uade.models;

import java.util.Date;
import java.util.List;
import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Carrito {

    private int id_carrito;
    private List<Producto> productos;
    private int cantidad;
    private Float precioTotal;
    private Float descuento;
    private Date fechaCreacion;
    private boolean carritoCreado;


    public Float getSubTotal(Float precio, Float descuento) {
        if (descuento == null) {
            // Por que le tengo q poner la f
            descuento = 0.0f;
        }
        return precio - (precio * descuento / 100);
    }
}
