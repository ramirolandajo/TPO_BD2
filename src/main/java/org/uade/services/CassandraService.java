package org.uade.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.uade.connections.CassandraDB;
import org.uade.exceptions.CassandraConnectionException;
import org.uade.models.Factura;
import org.uade.models.Producto;

import java.time.LocalDateTime;
import java.util.Locale;

public class CassandraService {

    private final CassandraDB cassandraDB;
    private final Session session;
    MongoService mongoService;

    public CassandraService(MongoService mongoService) throws CassandraConnectionException {
        this.mongoService = mongoService;
        this.cassandraDB = new CassandraDB();
        this.session = this.cassandraDB.getSession();
        this.mongoService = mongoService;
    }

    public void logCambiosProducto(Producto productoViejo, Producto productoNuevo, String tipoCambio, int operador) {
        session.execute("CREATE TABLE IF NOT EXISTS logCambiosProductos(idLog uuid, idProducto int, " +
                "descripcionVieja text, precioViejo float, descuentoViejo float, impuestoIvaViejo float, imagenVieja text, " +
                "descripcionNueva text, precioNuevo float, descuentoNuevo float, impuestoIvaNuevo float, imagenNueva text, " +
                "tipoCambio text, operador int, PRIMARY KEY(idProducto, idLog))");

        String cqlStatement = String.format(Locale.US,
                "INSERT INTO logCambiosProductos(idLog, idProducto, descripcionVieja, precioViejo, descuentoViejo, " +
                        "impuestoIvaViejo, imagenVieja, descripcionNueva, precioNuevo, descuentoNuevo, impuestoIvaNuevo, " +
                        "imagenNueva, tipoCambio, operador) VALUES (uuid(), %d, '%s', %.2f, %.2f, %.2f, '%s', '%s', %.2f," +
                        " %.2f, %.2f, '%s', '%s', %d)",
                productoViejo.getIdProducto(),
                productoViejo.getDescripcion(),
                productoViejo.getPrecio(),
                productoViejo.getDescuento(),
                productoViejo.getImpuestoIVA(),
                productoViejo.getImagen(),
                productoViejo.getDescripcion(),
                productoNuevo.getPrecio(),
                productoNuevo.getDescuento(),
                productoNuevo.getImpuestoIVA(),
                productoViejo.getImagen(),
                tipoCambio,
                operador
        );

        session.execute(cqlStatement);
    }

    public void verLogsCatalogo() {
        String statement = "SELECT * FROM logCambiosProductos";

        ResultSet result = session.execute(statement);

        // Construye un StringBuilder para formatear la salida
        StringBuilder sb = new StringBuilder();

        // Imprime los encabezados de columna (opcional)
        sb.append(String.format("%-36s %-12s %-20s %-10s %-10s %-10s %-20s %-20s %-10s %-10s %-10s %-20s %-20s %-10s%n",
                "\nidLog", "idProducto", "descripcionVieja", "precioViejo", "descuentoViejo", "impuestoIvaViejo",
                "imagenVieja", "descripcionNueva", "precioNuevo", "descuentoNuevo", "impuestoIvaNuevo",
                "imagenNueva", "tipoCambio", "operador"));

        // Recorre cada fila en el ResultSet
        for (Row row : result) {
            sb.append(String.format("%-40s %-10d %-20s %-10.2f %-10.2f %-10.2f %-20s %-20s %-10.2f %-10.2f %-10.2f %-20s %-20s %-10d%n",
                    row.getUUID("idLog"),
                    row.getInt("idProducto"),
                    row.getString("descripcionVieja"),
                    row.getFloat("precioViejo"),
                    row.getFloat("descuentoViejo"),
                    row.getFloat("impuestoIvaViejo"),
                    row.getString("imagenVieja"),
                    row.getString("descripcionNueva"),
                    row.getFloat("precioNuevo"),
                    row.getFloat("descuentoNuevo"),
                    row.getFloat("impuestoIvaNuevo"),
                    row.getString("imagenNueva"),
                    row.getString("tipoCambio"),
                    row.getInt("operador")));
        }

// Imprime el resultado formateado
        System.out.println(sb.toString());
    }

    public void logFactura(Factura factura) {
        session.execute("CREATE TABLE IF NOT EXISTS logFacturas(idLog uuid, idFactura int, idPedidoReferencia int, "
                + "idUsuario text, facturaPagada boolean, formaPago text, operador text, fecha_hora timestamp," +
                " monto float, PRIMARY KEY (idLog, fecha_hora))");

        String operador;
        if (factura.getFormaPago().equals("EFECTIVO")) {
            operador = "Empleado delivery (contra entrega)";
        } else if (factura.getFormaPago().equals("PUNTO_RETIRO")) {
            operador = "Empleado local";
        } else {
            operador = null;
        }

        LocalDateTime fecha_hora_actual = LocalDateTime.now();

        String cqlStatement = String.format(Locale.US, "INSERT INTO logFacturas (idLog, idFactura, idPedidoReferencia, idUsuario, " +
                        "facturaPagada, formaPago, operador, fecha_hora, monto) VALUES (uuid(), %d, %d, '%s', '%s', '%s', " +
                        "'%s', '%s', %.2f",
                factura.getIdFactura(),
                factura.getIdPedido(),
                factura.getIdUsuario(),
                factura.isFacturaPagada(),
                factura.getFormaPago(),
                operador,
                fecha_hora_actual,
                factura.getMonto()
        );

        session.execute(cqlStatement);
    }

    public void verLogFacturas() {
        String statement = "SELECT * FROM logFacturas";

        ResultSet result = session.execute(statement);

        // Construye un StringBuilder para formatear la salida
        StringBuilder sb = new StringBuilder();

        // Imprime los encabezados de columna (opcional)
        sb.append(String.format("%-36s %-12s %-20s %-10s %-10s %-10s %-20s %-20s",
                "\nidLog", "idFactura", "idPedidoReferencia", "facturaPagada", "formaPago", "operador",
                "fecha_hora", "monto"));

        // Recorre cada fila en el ResultSet
        for (Row row : result) {
            sb.append(String.format("%-40s %-10d %-20s %-10.2f %-10.2f %-10.2f %-20s",
                    row.getUUID("idLog"),
                    row.getInt("idFactura"),
                    row.getString("idPedidoReferencia"),
                    row.getFloat("facturaPagada"),
                    row.getFloat("formaPago"),
                    row.getFloat("operador"),
                    row.getString("imagenVieja")));
        }

        // Imprime el resultado formateado
        System.out.println(sb.toString());
    }

    public void close() {
        cassandraDB.close();
    }
}
