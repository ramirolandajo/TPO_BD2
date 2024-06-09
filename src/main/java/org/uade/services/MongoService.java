package org.uade.services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.internal.Iterables;
import org.bson.conversions.Bson;
import org.uade.connections.MongoDB;
import org.uade.exceptions.MongoConnectionException;
import org.uade.models.Factura;
import org.uade.models.Pedido;
import org.uade.models.Producto;
import org.uade.models.Usuario;

import java.util.*;

public class MongoService {
    private final MongoDatabase database;
    private MongoCollection<Producto> coleccionProductos;
    private MongoCollection<Usuario> coleccionUsuarios;
    private MongoCollection<Pedido> coleccionPedidos;
    private MongoCollection<Factura> coleccionFacturas;
    private CassandraService cassandraService;
    private RedisService redisService;

    Scanner sc = new Scanner(System.in).useLocale(Locale.US);

    public MongoService(CassandraService cassandraService, RedisService redisService) throws MongoConnectionException {
        this.database = MongoDB.getInstancia().getConnection();
        this.coleccionProductos = database.getCollection("Productos", Producto.class);
        this.coleccionUsuarios = database.getCollection("Usuarios", Usuario.class);
        this.coleccionPedidos = database.getCollection("Pedidos", Pedido.class);
        this.coleccionFacturas = database.getCollection("Facturas", Factura.class);
        this.cassandraService = cassandraService;
        this.redisService = redisService;

        this.coleccionProductos.replaceOne(Filters.eq("idProducto", 1), new Producto(
                1,
                "Guantes de box",
                2000f,
                200f,
                0.23f,
                "https://media.istockphoto.com/id/844311822/es/foto/render-3d-de-un-rojo-derecha-boxeo-guantes-aislados-sobre-fondo-blanco.jpg?s=612x612&w=0&k=20&c=IttNIFUFeDTS4bOAAu6n-ILAElahGtFI8hs4y91srxU="
        ), new ReplaceOptions().upsert(true));
        this.coleccionProductos.replaceOne(Filters.eq("idProducto", 2), new Producto(
                2,
                "Botines de futbol",
                1000f,
                0f,
                0.23f,
                "https://media.istockphoto.com/id/1404635567/es/foto/primer-plano-de-un-par-de-botas-de-f%C3%BAtbol-de-cuero-negro-aisladas-sobre-fondo-blanco.jpg?s=612x612&w=0&k=20&c=t66w4A4ySzgkh3G1N44Iz3UEzI4Wty6LwjX71wR8uQk="
        ), new ReplaceOptions().upsert(true));
        this.coleccionProductos.replaceOne(Filters.eq("idProducto", 3), new Producto(
                3,
                "Raqueta de tennis",
                5000f,
                1500f,
                0f,
                "https://media.istockphoto.com/id/1064972966/es/foto/render-3d-y-una-raqueta-de-tenis-solo-con-la-bola-de-un-amarillo-sobre-fondo-blanco.jpg?s=612x612&w=0&k=20&c=IWISK-DYcp_MsBqrz2ijp8agABPCLtKBxQl5fV7EZ-E="
        ), new ReplaceOptions().upsert(true));
        this.coleccionProductos.replaceOne(Filters.eq("idProducto", 4), new Producto(
                4,
                "Pelota de rugby",
                500f,
                100f,
                0.23f,
                "https://images.pexels.com/photos/13583551/pexels-photo-13583551.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
        ), new ReplaceOptions().upsert(true));
    }

    public void agregarUsuario(Usuario usuario) {
        this.coleccionUsuarios.insertOne(usuario);
        System.out.println("Usuario registrado con éxito!");
    }

    public Usuario recuperarUsuario(String documento) {
        Bson filter = Filters.eq("dni", documento);
        FindIterable<Usuario> elementsFound = this.coleccionUsuarios.find(filter);
        for (Usuario usuario : elementsFound)
            return usuario;
        return null;
    }

    public void agregarProductoAlCatalogo() {
        System.out.println("Ingrese los datos a del producto nuevo: ");

        System.out.print("ID: ");
        int idProducto = sc.nextInt();
        sc.nextLine();

        while (this.recuperarProducto(idProducto) != null) {
            System.out.print("Ya existe un producto con el ID " + idProducto + ". Ingrese otro: ");
            idProducto = sc.nextInt();
            sc.nextLine();
        }

        System.out.print("Descripción: ");
        String descripcion = sc.nextLine();

        System.out.print("Precio: ");
        float precio = sc.nextFloat();
        sc.nextLine();

        System.out.print("Descuento: ");
        float descuento = sc.nextFloat();
        sc.nextLine();

        System.out.print("Impuesto de IVA: ");
        float impuestoIVA = sc.nextFloat();
        sc.nextLine();

        System.out.print("Imagen: ");
        String imagen = sc.nextLine();

        this.coleccionProductos.insertOne(new Producto(idProducto, descripcion, precio, descuento, impuestoIVA, imagen));
        System.out.println("Producto agregado al catalogo con éxito!");
    }

    public Producto recuperarProducto(int idProducto) {
        Bson filter = Filters.eq("idProducto", idProducto);
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

        System.out.println("\nPresione Enter para continuar...");
        sc.nextLine();
    }

    public void actualizarProducto(int id) {

        System.out.println("Ingrese los datos a actualizar: ");
        System.out.print("\nDescripción: ");
        String descripcion = sc.nextLine();

        System.out.print("Precio: ");
        String inputPrecio = sc.nextLine();
        float precio = 0;

        if (!inputPrecio.isEmpty())
            precio = Float.parseFloat(inputPrecio);

        System.out.print("Descuento: ");
        String inputDescuento = sc.nextLine();
        float descuento = 0;

        if (!inputDescuento.isEmpty())
            descuento = Float.parseFloat(inputDescuento);

        System.out.print("Impuesto de IVA: ");
        String inputIVA = sc.nextLine();
        float impuestoIVA = 0;

        if (!inputIVA.isEmpty())
            impuestoIVA = Float.parseFloat(inputIVA);

        System.out.print("Imagen: ");
        String imagen = sc.nextLine();

        List<Bson> updates = new ArrayList<>(); // Lista de elementos a actualizar.

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
        if (imagen != null && !imagen.isEmpty()) {
            updates.add(Updates.set("imagen", imagen));
        }

        if (!(descripcion == null || descripcion.isEmpty()) || !inputPrecio.isEmpty() || !inputDescuento.isEmpty() ||
                !inputIVA.isEmpty() || !(imagen == null || imagen.isEmpty())) {
            System.out.print("Ingrese el tipo de cambio: ");
            String tipoCambio = sc.nextLine();

            System.out.print("Ingrese el operador a cargo: ");
            int idOperador = sc.nextInt();
            sc.nextLine();

            Bson filter = Filters.eq("idProducto", id);

            // Se recupera el producto antes de actualizarlo.
            Producto producto = recuperarProducto(id);
            Producto productoViejo = new Producto(producto.getIdProducto(), producto.getDescripcion(),
                    producto.getPrecio(), producto.getDescuento(), producto.getImpuestoIVA(), producto.getImagen());

            // Actualizamos el documento en mongo
            this.coleccionProductos.updateOne(filter, updates);

            // Se recupera el producto actualizado para imprimirlo.
            Producto productoActualizado = recuperarProducto(id);

            // Se logea el cambio del catálogo en Cassandra.
            cassandraService.logCambiosProducto(productoViejo, productoActualizado, tipoCambio, idOperador);

            System.out.println("Producto actualizado!");
            System.out.println(productoActualizado.getIdProducto() + " " + productoActualizado.getDescripcion() + " " + productoActualizado.getPrecio() + " " + productoActualizado.getImpuestoIVA() + " " + productoActualizado.getDescuento() + " " + productoActualizado.getImagen());
        } else {
            System.out.println("\nSe debe llenar algun campo para actualizar el producto!. No se ha realizado ningún cambio.");
            System.out.println();
        }
    }

    public void eliminarProductoDelCatalogo(int idProducto) {
        while (idProducto != -1) {
            Bson filter = Filters.eq("idProducto", idProducto);
            if (this.recuperarProducto(idProducto) != null) {
                this.coleccionProductos.deleteOne(filter);
                System.out.println("Producto " + idProducto + " eliminado con éxito!");
                idProducto = -1;
            } else {
                System.out.print("El producto con el ID " + idProducto + " no existe! Ingrese otro id (presione -1 para salir): ");
                idProducto = sc.nextInt();
                sc.nextLine();
            }
        }
    }

    public void generarPedido(String idUsuario) {
        Pedido pedido = new Pedido();
        pedido.setIdPedido((int) (Math.random() * 100000));

        Usuario usuario = recuperarUsuario(idUsuario);
        pedido.setUsuario(usuario);

        float precioTotal = 0.0f;
        float totalIVA = 0.0f;

        Map<Producto, Integer> itemsCarrito = redisService.recuperarCarrito(idUsuario);
        for (Map.Entry<Producto, Integer> entry : itemsCarrito.entrySet()) {
            Producto producto = entry.getKey();
            Integer cantidad = entry.getValue();
            // calculamos el iva del precio del producto, le sumamos el precio del producto, le descontamos el desucento y multiplicamos por la cantidad
            float subtotal = ((producto.getPrecio() * producto.getImpuestoIVA()) + producto.getPrecio() - producto.getDescuento()) * cantidad;

            // guardamos la cantidad de IVA para la factura (si es EMPRESA el usuario no abona el IVA)
            float subtotalIVA = (producto.getPrecio() * producto.getImpuestoIVA()) * cantidad;
            precioTotal += subtotal;
            totalIVA += subtotalIVA;
        }
        pedido.setPrecioTotal(precioTotal);
        Map<String, Integer> itemsCarritoFormateado = new HashMap<>();
        for (Map.Entry<Producto, Integer> entry : itemsCarrito.entrySet()) {
            Producto producto = entry.getKey();
            Integer cantidad = entry.getValue();
            itemsCarritoFormateado.put(String.valueOf(producto.getIdProducto()), cantidad);
        }
        pedido.setProductos(itemsCarritoFormateado);

        this.coleccionPedidos.insertOne(pedido);
        redisService.vaciarCarrito(idUsuario);
        System.out.println("\nPedido generado con éxito!");

        // Generamos la factura del pedido
        generarFactura(pedido, totalIVA);
    }

    public void generarFactura(Pedido pedido, float totalIVA) {
        // le pide directamente el medio de pago aunque sea que acaba de confirmar el carrito?

        Usuario usuario = pedido.getUsuario();
        float montoFactura;

        if (usuario.getTipoUsuario().equals("EMPRESA")) {
            montoFactura = pedido.getPrecioTotal() - totalIVA;
        } else {
            montoFactura = pedido.getPrecioTotal();
        }

        Factura factura = new Factura();
        factura.setIdFactura((int) (Math.random() * 100000));
        factura.setIdPedido(pedido.getIdPedido());
        factura.setIdUsuario(usuario.getDni());
        factura.setFacturaPagada(false);
        factura.setFormaPago(null);
        factura.setMonto(montoFactura);

        this.coleccionFacturas.insertOne(factura);
        cassandraService.logFactura(factura);
        System.out.println("Factura generada con exito!");
    }

    public void pagarFactura() {
        System.out.println("Ingrese el id de la factura que desea pagar");
        int idFactura = sc.nextInt();
        sc.nextLine();
        while (idFactura < 1) {
            System.out.println("El Id debe ser positivo! Vuelva a intentarlo");
            idFactura = sc.nextInt();
            sc.nextLine();
        }

        Factura facturaUsuario = this.recuperarFactura(idFactura);

        if (facturaUsuario != null) {
            if (facturaUsuario.isFacturaPagada()) {
                System.out.println("La factura con id: " + idFactura + " ya esta pagada");
                return;
            }

            Usuario usuario = this.recuperarUsuario(facturaUsuario.getIdUsuario());

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
            }

            if (formaPago.equals("CUENTA_CORRIENTE")) {
                if ((usuario.getCuentaCorriente() - facturaUsuario.getMonto()) >= 0) {
                    usuario.setCuentaCorriente(usuario.getCuentaCorriente() - facturaUsuario.getMonto());

                    Bson filterUsuario = Filters.eq("dni", usuario.getDni());
                    Bson updateCuentaCorriente = Updates.set("cuentaCorriente", usuario.getCuentaCorriente());

                    this.coleccionUsuarios.updateOne(filterUsuario, updateCuentaCorriente);
                }
                else {
                    System.out.println("Saldo en cuenta corriente insuficiente.");
                    return;
                }
            }

            facturaUsuario.setFormaPago(formaPago);
            facturaUsuario.setFacturaPagada(true);

            Bson filter = Filters.eq("idFactura", idFactura);
            List<Bson> updateFormaPagoAndFacturaPagada = new ArrayList<>();
            updateFormaPagoAndFacturaPagada.add(Updates.set("formaPago", formaPago));
            updateFormaPagoAndFacturaPagada.add(Updates.set("facturaPagada", true));

            this.coleccionFacturas.updateOne(filter, updateFormaPagoAndFacturaPagada);
            cassandraService.logFactura(facturaUsuario);
            System.out.println("Factura pagada!");

        } else
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
        Bson filter = Filters.eq("idUsuario", idUsuario);

        Iterable<Factura> facturas = this.coleccionFacturas.find(filter);
        if (facturas.iterator().hasNext()) {
            for (Factura fac : facturas) {
                System.out.printf("ID Factura: %d ID Pedido: %d ID Usuario: %s Factura pagada: %b Forma pago: %s Monto: %f",
                        fac.getIdFactura(),
                        fac.getIdPedido(),
                        fac.getIdUsuario(),
                        fac.isFacturaPagada(),
                        fac.getFormaPago(),
                        fac.getMonto()
                );
                System.out.println();
            }
        } else
            System.out.println("No hay facturas para el usuario con dni: " + idUsuario);
    }

    public void close() {
        MongoDB.getInstancia().close();
    }
}