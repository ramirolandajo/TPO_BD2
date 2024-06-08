package org.uade.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Usuario {
    private String dni;
    private String nombreCompleto;
    private String direccion;
    private Float cuentaCorriente;
    private String tipoUsuario;
}
