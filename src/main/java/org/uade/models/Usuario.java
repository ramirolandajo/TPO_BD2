package org.uade.models;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Usuario {
    private int idUsuario;
    private String nombreCompleto;
    private String documento;
    private String direccion;
    private Float cuentaCorriente;
    private List<Carrito> carritosDisponibles;

}
