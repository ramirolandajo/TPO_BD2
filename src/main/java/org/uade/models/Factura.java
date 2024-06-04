package org.uade.models;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Factura {
    private int id_factura;
    private String estado;
    private String formaPago;
}
