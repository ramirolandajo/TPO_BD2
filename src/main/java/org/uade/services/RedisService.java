package org.uade.services;

import org.uade.connections.RedisDB;
import org.uade.exceptions.RedisConnectionException;
import redis.clients.jedis.Jedis;

public class RedisService {
    private static RedisService instancia;
    private final Jedis database;

    private RedisService() throws RedisConnectionException {
        this.database = RedisDB.getInstancia().getConection();
    }
    public static RedisService getInstance() throws RedisConnectionException{
        if(instancia == null)
            instancia = new RedisService();
        return instancia;
    }

    // Clase que se utiliza para ver la hora de inicio de sesion del usuario.
    public void iniciarSesion(String idUsuario){
        Long tiempoInicio = System.currentTimeMillis();
        this.database.hset(idUsuario, "Inicio", String.valueOf(tiempoInicio));
    }

    // Clase que se utiliza para ver la hora de cerrado de sesion del usuario.
    public void cerrarSesion(String idUsuario){
        Long tiempoFin = System.currentTimeMillis();
        this.database.hset(idUsuario, "Cerrado", String.valueOf(tiempoFin));
    }

    public Long tiempoDeSesion(String idUsuario){
        Long tiempoInicio = Long.parseLong(database.hget(idUsuario, "Inicio"));
        Long tiempoFin = Long.parseLong(database.hget(idUsuario, "Cerrado"));

        return tiempoFin-tiempoInicio;
    }

    /*
    public void agregarProductoCarrito(String idCarrito){
        database.sadd("carrito:" + idCarrito, )
    }
    */
}
