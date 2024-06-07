package org.uade.services;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.conversions.*;
import org.uade.connections.MongoDB;
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
    Scanner sc = new Scanner(System.in);

    public MongoService() throws MongoConnectionException {
        this.database = MongoDB.getInstancia().getConnection();
        this.coleccionProductos = database.getCollection("Productos", Producto.class);
        this.coleccionUsuarios  = database.getCollection("Usuarios", Usuario.class);
        this.coleccionPedidos   = database.getCollection("Pedidos", Pedido.class);
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

        System.out.println("Comentario: ");
        String comentario = sc.nextLine();

        this.coleccionProductos.insertOne(new Producto(++Producto.contadorId,descripcion, precio, descuento, impuestoIVA, imagen));
        System.out.println("Producto agregado al catalogo con éxito!");
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

        System.out.println("Comentario: ");
        String comentario= sc.nextLine();

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
        if (comentario != null) {
            updates.add(Updates.set("comentario", comentario));
        }

        FindIterable<Producto> productoEncontrado = coleccionProductos.find(filter);
        for(Producto p : productoEncontrado) {
            this.coleccionProductos.updateOne(filter, updates);
            System.out.println("Producto actualizado!");
            System.out.println(p.getIdProducto() + " " + p.getDescripcion() + " " + p.getPrecio() + " " + p.getImpuestoIVA() + " " + p.getDescuento() + " " + p.getImagen());
        }
    }

    public void generarPedido(Pedido pedido){
        this.coleccionPedidos.insertOne(pedido);
        System.out.println("Pedido generado con éxito!");
    }
}
