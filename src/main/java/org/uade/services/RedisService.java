package org.uade.services;

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
    public void iniciarSesion(String idUsuario) {
        tiempoInicio = LocalDateTime.now();
        this.database.hset("usuario:"+idUsuario, "Inicio", String.valueOf(tiempoInicio));
    }

    // Clase que se utiliza para ver la hora de cerrado de sesion del usuario.
    public void cerrarSesion(String idUsuario) {
        Duration duration = Duration.between(tiempoInicio, LocalDateTime.now());
        String categorizacion;
        if ((duration.getSeconds() / 60) >= 240) {
            categorizacion = "TOP";
        }
        else if ((duration.getSeconds() / 60) >= 120 && (duration.getSeconds() / 60) < 240) {
            categorizacion = "MEDIUM";
        }
        else {
            categorizacion = "LOW";
        }
        this.database.hset("usuario:"+idUsuario, "Categorizacion: ", categorizacion);
    }

    /*
    public void agregarProductoCarrito(String idCarrito){
        database.sadd("carrito:" + idCarrito, )
    }
    */
}
