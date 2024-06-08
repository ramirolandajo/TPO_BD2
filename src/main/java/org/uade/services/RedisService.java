package org.uade.services;

import com.mongodb.client.model.Filters;
import org.uade.connections.RedisDB;
import org.uade.exceptions.CassandraConnectionException;
import org.uade.exceptions.MongoConnectionException;
import org.uade.exceptions.RedisConnectionException;
import org.uade.models.Producto;
import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class RedisService {
    private final Jedis database;
    private LocalDateTime tiempoInicio;
    MongoService mongoService;

    public RedisService(MongoService mongo) throws RedisConnectionException, MongoConnectionException, CassandraConnectionException {
        this.database = RedisDB.getInstancia().getConnection();
        this.mongoService = mongo;
    }

    // Clase que se utiliza para ver la hora de inicio de sesion del usuario.
    public void iniciarSesion(String idUsuario){
        // en un futuro podemos guardar la fecha de inicio de sesion y chequear si es vacio, el mismo dia u otro dia
        tiempoInicio = LocalDateTime.now();
        this.database.hset("usuario:"+idUsuario, "Inicio", String.valueOf(tiempoInicio)); // Este podria borrarse y colocarse en el cerrarSesion.
        System.out.println("Sesión iniciada con éxito!");
    }

    // Clase que se utiliza para ver la hora de cerrado de sesion del usuario.
    public void cerrarSesion(String idUsuario){

        Duration duration = Duration.between(tiempoInicio, LocalDateTime.now());

        long minutos = duration.toMinutes();

        if(minutos >= 240)
            this.database.hset("usuario:"+idUsuario, "Categorización", "TOP");
        else if(minutos >= 120)
            this.database.hset("usuario:"+idUsuario, "Categorización", "MEDIUM");
        else
            this.database.hset("usuario:"+idUsuario, "Categorización", "LOW");

        System.out.println("Sesión cerrada con éxito!");
    }

    public void agregarProductoCarrito(String idUsuario, int idProducto, int cantidad) {
        this.database.hset("carrito:"+idUsuario, String.valueOf(idProducto), String.valueOf(cantidad));
    }

    public Map<Producto, Integer> recuperarCarrito(String idUsuario){
        Map<String, String> carritoUsuario = this.database.hgetAll("carrito:"+idUsuario);

        Map<Producto, Integer> itemsCarrito = new HashMap<>();

        for (Map.Entry<String, String> entry : carritoUsuario.entrySet()) {
            Producto producto = mongoService.recuperarProducto(Filters.eq("idProducto", entry.getKey()));
            Integer cantidad = Integer.valueOf(entry.getValue());
            itemsCarrito.put(producto, cantidad);
        }
        return itemsCarrito;
    }
}
