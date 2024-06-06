package org.uade.services;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.conversions.*;
import org.uade.connections.MongoDB;
import org.uade.exceptions.MongoConnectionException;
import org.uade.models.*;

import java.util.ArrayList;
import java.util.List;

public class MongoService {
    private static MongoService instancia;
    private final MongoDatabase database;
    private MongoCollection<Producto> coleccionProductos;
    private MongoCollection<Usuario> coleccionUsuarios;
    private MongoCollection<Pedido> coleccionPedidos;

    private MongoService() throws MongoConnectionException {
        this.database = MongoDB.getInstancia().getConnection();
        this.coleccionProductos = database.getCollection("Productos", Producto.class);
        this.coleccionUsuarios  = database.getCollection("Usuarios", Usuario.class);
        this.coleccionPedidos   = database.getCollection("Pedidos", Pedido.class);
    }

    public static MongoService getInstancia() throws MongoConnectionException {
        if(instancia == null)
            instancia = new MongoService();
        return instancia;
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

    public void agregarProductoAlCatalogo(Producto producto){
        this.coleccionProductos.insertOne(producto);
        System.out.println("Producto agregado al catalogo con éxito!");
    }

    public List<Producto> recuperarCatalogo(){
        List<Producto> catalogo = new ArrayList<>();
        FindIterable<Producto> productos = coleccionProductos.find();
        for(Producto p : productos)
            catalogo.add(p);

        return catalogo;
    }

    public void generarPedido(Pedido pedido){
        this.coleccionPedidos.insertOne(pedido);
        System.out.println("Pedido generado con éxito!");
    }
}
