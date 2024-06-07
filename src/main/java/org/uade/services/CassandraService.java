package org.uade.services;

import com.datastax.driver.core.Session;
import org.uade.connections.CassandraDB;
import org.uade.exceptions.CassandraConnectionException;
import org.uade.models.Producto;

public class CassandraService {

    private final CassandraDB cassandraDB;
    private final Session session;

    public CassandraService() throws CassandraConnectionException {
        this.cassandraDB = new CassandraDB();
        this.session = this.cassandraDB.getSession();

        session.execute("CREATE KEYSPACE IF NOT EXISTS tpo_bd2 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
        session.execute("USE tpo_bd2");
    }

    public void logCambiosProducto(Producto productoViejo, Producto productoNuevo, String tipoCambio, int operador) {
        session.execute("CREATE TABLE IF NOT EXISTS logCambiosProductos(idLog uuid, idProducto int, " +
                "descripcionVieja text, precioViejo float, descuentoViejo float, impuestoIvaViejo float, imagenVieja text, " +
                "comentariosViejos text, descripcionNueva text, precioNuevo float, descuentoNuevo float, impuestoIvaNuevo" +
                " float, imagenNueva text, comentariosNuevos text, tipoCambio text, operador int)");

        String cqlStatement = "INSERT INTO TABLE logCambiosProductos(idLog, idProducto int, "
                + "descripcionVieja text, precioViejo float, descuentoViejo float, impuestoIvaViejo float, imagenVieja text, "
                + "comentariosViejos text, descripcionNueva text, precioNuevo float, descuentoNuevo float, impuestoIvaNuevo"
                + " float, imagenNueva text, comentariosNuevos text, tipoCambio text, operador int)" +
                "VALUES (uuid(), "+ productoViejo.getIdProducto() + ", " + productoViejo.getDescripcion() + ", " +
                productoViejo.getPrecio() + ", " + productoViejo.getDescuento() + ", " + productoViejo.getImpuestoIVA()
                 + ", " + productoViejo.getImagen() + ", " + ", " + productoNuevo.getDescripcion()
                + ", " + productoNuevo.getPrecio() + ", " + productoNuevo.getDescuento() + ", " + productoNuevo.getImpuestoIVA()
                + ", " + productoNuevo.getImagen() + ", " +  ", " + tipoCambio + ", " + operador
                + ")";
    }

    public void verLogsCatalogo(){
        String statement = "SELECT * FROM logCambiosProductos";
        session.execute(statement);
    }
}
