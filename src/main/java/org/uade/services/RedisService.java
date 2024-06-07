package org.uade.services;

import jdk.vm.ci.meta.Local;
import org.uade.connections.RedisDB;
import org.uade.exceptions.RedisConnectionException;
import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.time.LocalDateTime;

public class RedisService {
    private final Jedis database;
    private LocalDateTime tiempoInicio;

    public RedisService() throws RedisConnectionException {
        this.database = RedisDB.getInstancia().getConnection();
    }

    // Clase que se utiliza para ver la hora de inicio de sesion del usuario.
    public void iniciarSesion(String idUsuario){
        tiempoInicio = LocalDateTime.now();
        this.database.hset(idUsuario, "Inicio", String.valueOf(tiempoInicio)); // Este podria borrarse y colocarse en el cerrarSesion.
        System.out.println("Sesión iniciada con éxito!");
    }

    // Clase que se utiliza para ver la hora de cerrado de sesion del usuario.
    public void cerrarSesion(String idUsuario){

        Duration duration = Duration.between(tiempoInicio, LocalDateTime.now());

        long minutos = duration.toMinutes();

        if(minutos >= 240)
            this.database.hset(idUsuario, "Categorización", "TOP");
        else if(minutos >= 120)
            this.database.hset(idUsuario, "Categorización", "MEDIUM");
        else
            this.database.hset(idUsuario, "Categorización", "LOW");

        System.out.println("Sesión cerrada con éxito!");
    }

    /*
    public void agregarProductoCarrito(String idCarrito){
        database.sadd("carrito:" + idCarrito, )
    }
    */
}
