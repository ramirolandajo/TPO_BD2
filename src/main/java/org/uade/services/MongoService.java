package org.uade.services;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.conversions.*;
import org.uade.connections.CassandraDB;
import org.uade.connections.MongoDB;
import org.uade.exceptions.CassandraConnectionException;
import org.uade.exceptions.MongoConnectionException;
import org.uade.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MongoService {
    private final MongoDatabase database;
    private MongoCollection<Producto> coleccionProductos;
    private MongoCollection<Usuario> coleccionUsuarios;
    private MongoCollection<Pedido> coleccionPedidos;
    private MongoCollection<Factura> coleccionFacturas;
    CassandraService cassandraDatabase = new CassandraService();
    Scanner sc = new Scanner(System.in);

    public MongoService() throws MongoConnectionException, CassandraConnectionException {
        this.database = MongoDB.getInstancia().getConnection();
        this.coleccionProductos = database.getCollection("Productos", Producto.class);
        this.coleccionUsuarios  = database.getCollection("Usuarios", Usuario.class);
        this.coleccionPedidos   = database.getCollection("Pedidos", Pedido.class);
        this.coleccionFacturas = database.getCollection("Facturas", Factura.class);
    }

    public void agregarUsuario (Usuario usuario){
        this.coleccionUsuarios.insertOne(usuario);
        System.out.println("Usuario registrado con éxito!");
    }

    public Usuario recuperarUsuario(String documento){
        Bson filter = Filters.eq("documento", documento);
        FindIterable<Usuario> elementsFound = this.coleccionUsuarios.find(filter);
        for(Usuario usuario : elementsFound)
            return usuario;
        return null;
    }

    public void agregarProductoAlCatalogo(){
        System.out.println("Ingrese los datos a del producto nuevo: ");

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

        this.coleccionProductos.insertOne(new Producto(++Producto.contadorId,descripcion, precio, descuento, impuestoIVA, imagen));
        System.out.println("Producto agregado al catalogo con éxito!");
    }

    public Producto recuperarProducto(Bson filter){
        FindIterable<Producto> productoEncontrado = this.coleccionProductos.find(filter);
        for(Producto producto : productoEncontrado)
            return producto;
        return null;
    }

    public void recuperarCatalogo(){
        FindIterable<Producto> productos = coleccionProductos.find();
        for(Producto p : productos) {
            System.out.println(p.getIdProducto() + " " + p.getDescripcion() + " " + p.getPrecio() + " " + p.getImpuestoIVA() + " " + p.getDescuento() + " " + p.getImagen());
            System.out.println();
        }
    }

    public void modificarProducto(int id){

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

        Producto productoViejo = recuperarProducto(filter); // Se recupera el producto antes de actualizarlo.

        FindIterable<Producto> productoEncontrado = coleccionProductos.find(filter);
        for(Producto p : productoEncontrado)
            this.coleccionProductos.updateOne(filter, updates); // Se actualiza el producto.


        Producto productoActualizado = recuperarProducto(filter); // Se recupera el producto actualizado para imprimirlo.

        System.out.println("Ingrese el tipo de cambio: ");
        String tipoCambio = sc.nextLine();

        System.out.println("Ingrese el operador a cargo: "); // Chequear si el operador lo pasamos a String para indicar el rol
        int idOperador= sc.nextInt();                        // (cajero, delivery, etc.) o lo dejamos con Id.

        cassandraDatabase.logCambiosProducto(productoViejo, productoActualizado, tipoCambio, idOperador); // Se logea el cambio del catálogo en Cassandra.

        System.out.println("Producto actualizado!");
        System.out.println(productoActualizado.getIdProducto() + " " + productoActualizado.getDescripcion() + " " + productoActualizado.getPrecio() + " " + productoActualizado.getImpuestoIVA() + " " + productoActualizado.getDescuento() + " " + productoActualizado.getImagen());
    }

    public void generarPedido(Pedido pedido){
        this.coleccionPedidos.insertOne(pedido);
        System.out.println("Pedido generado con éxito!");
    }


    public void generarFactura(){
        System.out.println("Ingrese el código del pedido");
        int idPedido = sc.nextInt();

        System.out.println("Ingrese el medio de pago: ");
        String medioDePago = sc.nextLine();

        this.coleccionFacturas.insertOne(new Factura(++Factura.contadorId, idPedido, false, medioDePago));
    }

    public void recuperarFacturas() {
        Iterable<Factura> facturas = this.coleccionFacturas.find();
        for (Factura fac : facturas) {
            System.out.println(fac.getIdFactura() + " " + fac.isFacturaPagada() + " " + fac.getFormaPago());
            System.out.println();
        }
    }
}
