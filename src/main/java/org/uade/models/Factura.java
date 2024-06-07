package org.uade.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Factura {
    public static int contadorId;
    private int idFactura;
    private int idPedido;
    private boolean facturaPagada;
    private String formaPago;
}
