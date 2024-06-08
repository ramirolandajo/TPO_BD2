package org.uade.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Factura {
    public static int contadorId = 0;
    private int idFactura;
    private int idPedido;
    private String idUsuario;
    private boolean facturaPagada;
    private String formaPago;
    private float monto;
}
