package org.uade.models;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Usuario {
    private String documento;
    private String nombreCompleto;
    private String direccion;
    private Float cuentaCorriente;
}
