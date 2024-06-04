package org.uade.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Factura {
    private int idFactura;
    private boolean facturaPagada;
    private String formaPago;
}
