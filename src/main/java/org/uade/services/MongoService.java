package org.uade.services;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.conversions.*;
import org.uade.connections.MongoDB;
import org.uade.exceptions.*;
import org.uade.models.*;

import java.util.*;

public class MongoService {
    private final MongoDatabase database;
    private MongoCollection<Producto> coleccionProductos;
    private MongoCollection<Usuario> coleccionUsuarios;
    private MongoCollection<Pedido> coleccionPedidos;
    private MongoCollection<Factura> coleccionFacturas;

    private CassandraService cassandraService;
    private RedisService redisService ;

    Scanner sc = new Scanner(System.in).useLocale(Locale.US);

    public MongoService(CassandraService cassandraService, RedisService redisService) throws MongoConnectionException{
        this.database = MongoDB.getInstancia().getConnection();
        this.coleccionProductos = database.getCollection("Productos", Producto.class);
        this.coleccionUsuarios = database.getCollection("Usuarios", Usuario.class);
        this.coleccionPedidos = database.getCollection("Pedidos", Pedido.class);
        this.coleccionFacturas = database.getCollection("Facturas", Factura.class);
        this.cassandraService = cassandraService;
        this.redisService = redisService;
    }

    public void agregarUsuario(Usuario usuario) {
        this.coleccionUsuarios.insertOne(usuario);
        System.out.println("Usuario registrado con éxito!");
    }

    public Usuario recuperarUsuario(String documento) {
        Bson filter = Filters.eq("documento", documento);
        FindIterable<Usuario> elementsFound = this.coleccionUsuarios.find(filter);
        for (Usuario usuario : elementsFound)
            return usuario;
        return null;
    }

    public void agregarProductoAlCatalogo() {
        System.out.println("Ingrese los datos a del producto nuevo: ");

        System.out.println("Descripción: ");
        String descripcion = sc.nextLine();

        System.out.println("Precio: ");
        float precio = sc.nextFloat();
        sc.nextLine();

        System.out.println("Descuento: ");
        float descuento = sc.nextFloat();
        sc.nextLine();

        System.out.println("Impuesto de IVA: ");
        float impuestoIVA = sc.nextFloat();
        sc.nextLine();

        System.out.println("Imagen: ");
        String imagen = sc.nextLine();

        int idProducto = ++Producto.contadorId;
        System.out.println("ID: " + idProducto);
        this.coleccionProductos.insertOne(new Producto(idProducto, descripcion, precio, descuento, impuestoIVA, imagen));
        System.out.println("Producto agregado al catalogo con éxito!");
    }

    public Producto recuperarProducto(Bson filter) {
        FindIterable<Producto> productoEncontrado = this.coleccionProductos.find(filter);
        for (Producto producto : productoEncontrado)
            return producto;
        return null;
    }

    public void recuperarCatalogo() {
        FindIterable<Producto> productos = coleccionProductos.find();
        System.out.printf("%-5s %-25s %-15s %-10s %-15s %-10s\n", "ID", "DESCRIPCION", "PRECIO", "IVA", "DESCUENTO", "IMAGEN");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        for (Producto p : productos) {
            System.out.printf("%-5d %-25s %-15.2f %-10.2f %-10.2f %-10s\n",
                    p.getIdProducto(), p.getDescripcion(), p.getPrecio(), p.getImpuestoIVA(), p.getDescuento(),
                    p.getImagen());
        }
    }

    public void actualizarProducto(int id) {

        System.out.println("Ingrese los datos a actualizar: ");
        System.out.println("\nDescripción: ");
        String descripcion = sc.nextLine();

        System.out.println("Precio: ");
        float precio = sc.nextFloat();

        System.out.println("Descuento: ");
        float descuento = sc.nextFloat();

        System.out.println("Impuesto de IVA: ");
        float impuestoIVA = sc.nextFloat();

        System.out.println("Imagen: ");
        String imagen = sc.nextLine();

        Bson filter = Filters.eq("idProducto", id);
        List<Bson> updates = new ArrayList<>();

        if (descripcion != null && !descripcion.isEmpty()) {
            updates.add(Updates.set("descripcion", descripcion));
        }
        if (precio != 0) {
            updates.add(Updates.set("precio", precio));
        }
        if (descuento != 0) {
            updates.add(Updates.set("descuento", descuento));
        }
        if (impuestoIVA != 0) {
            updates.add(Updates.set("impuestoIVA", impuestoIVA));
        }
        if (imagen != null && imagen.isEmpty()) {
            updates.add(Updates.set("imagen", imagen));
        }

        // Se recupera el producto antes de actualizarlo.
        Producto productoViejo = recuperarProducto(filter);

        // Actualizamos el documento en mongo
        this.coleccionProductos.updateOne(filter, updates);

        // Se recupera el producto actualizado para imprimirlo.
        Producto productoActualizado = recuperarProducto(filter);

        System.out.println("Ingrese el tipo de cambio: ");
        String tipoCambio = sc.nextLine();

        // Chequear si el operador lo pasamos a String para indicar el rol
        System.out.println("Ingrese el operador a cargo: ");
        // (cajero, delivery, etc.) o lo dejamos con Id.
        int idOperador = sc.nextInt();

        // Se logea el cambio del catálogo en Cassandra.
        cassandraService.logCambiosProducto(productoViejo, productoActualizado, tipoCambio, idOperador);

        System.out.println("Producto actualizado!");
        System.out.println(productoActualizado.getIdProducto() + " " + productoActualizado.getDescripcion() + " " + productoActualizado.getPrecio() + " " + productoActualizado.getImpuestoIVA() + " " + productoActualizado.getDescuento() + " " + productoActualizado.getImagen());
    }

    public void generarPedido(String idUsuario) throws MongoConnectionException, CassandraConnectionException, RedisConnectionException {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(++Pedido.contadorId);

        Usuario usuario = recuperarUsuario(idUsuario);
        pedido.setUsuario(usuario);

        float precioTotal = 0.0f;
        float totalIVA = 0.0f;

        Map<Producto, Integer> itemsCarrito = redisService.recuperarCarrito(idUsuario);
        for (Map.Entry<Producto, Integer> entry : itemsCarrito.entrySet()) {
            Producto producto = entry.getKey();
            Integer cantidad = entry.getValue();
            float subtotal = ((producto.getPrecio() - producto.getDescuento()) + producto.getImpuestoIVA()) * cantidad;
            float subtotalIVA = (producto.getImpuestoIVA() * cantidad);
            precioTotal += subtotal;
            totalIVA += subtotalIVA;
        }
        pedido.setPrecioTotal(precioTotal);
        pedido.setProductos(itemsCarrito);

        this.coleccionPedidos.insertOne(pedido);
        System.out.println("Pedido generado con éxito!");

        // Generamos la factura del pedido
        generarFactura(pedido, totalIVA);
    }

    public Pedido recuperarPedido(int idPedido) {
        Bson filter = Filters.eq("idPedido", idPedido);
        FindIterable<Pedido> pedidoEncontrado = this.coleccionPedidos.find(filter);
        for (Pedido pedido : pedidoEncontrado) {
            return pedido;
        }
        return null;
    }

    public void generarFactura(Pedido pedido, float totalIVA) throws MongoConnectionException, CassandraConnectionException {
        // le pide directamente el medio de pago aunque sea que acaba de confirmar el carrito?
        System.out.println("Ingrese el medio de pago: ");
        System.out.println("1. Para abonar en efectivo");
        System.out.println("2. Para abonar en tarjeta");
        System.out.println("3. Para abonar en cuenta corriente");
        System.out.println("4. Para abonar en punto de retiro");
        int medioDePago = sc.nextInt();
        while (medioDePago != 1 && medioDePago != 2 && medioDePago != 3 && medioDePago != 4) {
            System.out.println("Opcion no valida. Vuelva a ingresar una opcion valida.");
            medioDePago = sc.nextInt();
        }
        String formaPago = "";

        switch (medioDePago) {
            case 1: {
                formaPago = "EFECTIVO";
                break;
            }
            case 2: {
                formaPago = "TARJETA";
                break;
            }
            case 3: {
                formaPago = "CUENTA_CORRIENTE";
                break;
            }
            case 4: {
                formaPago = "PUNTO_RETIRO";
                break;
            }
            default: {
                break;
            }
        }
        Usuario usuario = pedido.getUsuario();
        float montoFactura;

        if (usuario.getTipoUsuario().equals("EMPRESA")) {
            montoFactura = pedido.getPrecioTotal() - totalIVA;
        } else {
            montoFactura = pedido.getPrecioTotal();
        }

        Factura factura = new Factura();
        factura.setIdFactura(++Factura.contadorId);
        factura.setIdPedido(pedido.getIdPedido());
        factura.setFacturaPagada(false);
        factura.setFormaPago(formaPago);
        factura.setMonto(montoFactura);

        this.coleccionFacturas.insertOne(factura);
    }

    public void pagarFactura(){
        System.out.println("Ingrese el id de la factura que desea pagar");
        int idFactura = sc.nextInt();

        while(idFactura < 1){
            System.out.println("El Id debe ser positivo! Vuelva a intentarlo");
            idFactura = sc.nextInt();
        }

        Factura facturaUsuario = this.recuperarFactura(idFactura);

        if(facturaUsuario != null){
            Usuario usuario = this.recuperarUsuario(facturaUsuario.getIdUsuario());

            if((usuario.getCuentaCorriente() - facturaUsuario.getMonto()) > 0){
                facturaUsuario.setFacturaPagada(true);
                usuario.setCuentaCorriente(usuario.getCuentaCorriente() - facturaUsuario.getMonto());
            }

        }else
            System.out.println("La factura no se encuentra registrada en la base de datos!");
    }

    public Factura recuperarFactura(int idFactura) {
        Bson filter = Filters.eq("idFactura", idFactura);

        Iterable<Factura> facturas = this.coleccionFacturas.find(filter);
        for (Factura fac : facturas) {
            return fac;
        }

        return null;
    }

    public void recuperarFacturasUsuario(String idUsuario) {
        Iterable<Factura> facturas = this.coleccionFacturas.find();
        for (Factura fac : facturas) {
            System.out.println(fac.getIdFactura() + " " + fac.isFacturaPagada() + " " + fac.getFormaPago());
            System.out.println();
        }
    }
}