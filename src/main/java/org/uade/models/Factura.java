package org.uade.models;

public class Factura {

    private int id_factura;
    private String estado;
    private String formaPago;

    public Factura() {
    }

    public Factura(int id_factura, String estado, String formaPago) {
        this.id_factura = id_factura;
        this.estado = estado;
        this.formaPago = formaPago;
    }

    public int getId_factura() {
        return id_factura;
    }

    public String getEstado() {
        return estado;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setId_factura(int id_factura) {
        this.id_factura = id_factura;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }
}
