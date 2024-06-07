package org.uade.models;

import java.util.Date;
import java.util.List;
import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Carrito {
    private int idCarrito;
    private List<Producto> productos;
    private int cantidad;
    private Float precioTotal;
    private Float descuento;
    private Date fechaCreacion;
    private boolean carritoCreado;
}
