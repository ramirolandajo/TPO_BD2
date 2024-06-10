package org.uade.connections;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.uade.exceptions.CassandraConnectionException;

public class CassandraDB {

    private Cluster cluster;

    private Session session;

    public CassandraDB() throws CassandraConnectionException {
        try {
            cluster = Cluster.builder().addContactPoints("127.0.0.1").build();
            session = cluster.connect("system");

            session.execute("CREATE KEYSPACE IF NOT EXISTS tpo_bd2 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
            session.execute("USE tpo_bd2");

            session.execute("CREATE TABLE IF NOT EXISTS logCambiosProductos(idLog uuid, idProducto int, " +
                    "descripcionVieja text, precioViejo float, descuentoViejo float, impuestoIvaViejo float, imagenVieja text, " +
                    "descripcionNueva text, precioNuevo float, descuentoNuevo float, impuestoIvaNuevo float, imagenNueva text, " +
                    "tipoCambio text, operador int, PRIMARY KEY(idProducto, idLog))");

            session.execute("CREATE TABLE IF NOT EXISTS logFacturas(idLog uuid, idFactura int, idPedidoReferencia int, "
                    + "idUsuario text, facturaPagada boolean, formaPago text, operador text, fecha_hora timestamp," +
                    " monto float, PRIMARY KEY (idLog, fecha_hora))");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new CassandraConnectionException("Error de conexión con Cassandra");
        }
    }

    public Session getSession() {
        return session;
    }

    public void close() {
        if (session != null) {
            session.close();
        }
        if (cluster != null) {
            cluster.close();
        }
    }
}
