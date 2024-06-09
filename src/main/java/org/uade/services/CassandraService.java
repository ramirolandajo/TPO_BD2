package org.uade.services;

import com.datastax.driver.core.Session;
import org.uade.connections.CassandraDB;
import org.uade.exceptions.CassandraConnectionException;
import org.uade.models.Factura;
import org.uade.models.Pedido;
import org.uade.models.Producto;

import java.util.Locale;
import java.util.UUID;

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
        session.execute(statement);
    }

    public void logFactura(Factura factura) {
        session.execute("CREATE TABLE IF NOT EXISTS logFacturas(idLog uuid, idFactura int, idPedidoReferencia int, "
                + "idUsuario text, facturaPagada boolean, formaPago text, operador text, fecha_hora timestamp," +
                " monto float, PRIMARY KEY (idLog, fecha_hora))");

        Pedido pedidoReferencia = mongoService.recuperarPedido(factura.getIdPedido());

        String operador;
        if (factura.getFormaPago().equals("efectivo")) {
            operador = "Empleado delivery (contra entrega)";
        } else if (factura.getFormaPago().equals("punto_retiro")) {
            operador = "Empleado local";
        } else {
            operador = null;
        }
        String cqlStatement = "INSERT INTO logFacturas(idLog, idFactura, idPedidoReferencia, idUsuario, " +
                "facturaPagada, formaPago, operador, fecha_hora, monto) VALUES (uuid(), " + factura.getIdFactura()
                + ", " + factura.getIdPedido() + ", " + pedidoReferencia.getUsuario().getDni()
                + ", " + factura.isFacturaPagada() + ", " + factura.getFormaPago() + ", " + operador
                + ", toTimestamp(now()), " + factura.getMonto() + ")";

        session.execute(cqlStatement);
    }

    public void verLogFacturas() {
        String statement = "SELECT * FROM logFacturas";
        session.execute(statement);
    }

    public void close() {
        cassandraDB.close();
    }
 }
