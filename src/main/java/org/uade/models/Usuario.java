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
    private List<Carrito> carritosDisponibles;

    public Usuario(String nombreCompleto, String documento, String direccion, Float cuentaCorriente){
        this.nombreCompleto = nombreCompleto;
        this.documento = documento;
        this.direccion = direccion;
        this.cuentaCorriente = cuentaCorriente;
    }
}
