package org.uade.connections;

import org.uade.exceptions.RedisConnectionException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDB {

    private static RedisDB instancia;
    private JedisPool pool;

    private RedisDB() {
        pool = new JedisPool("localhost", 6379);
    }

    public static RedisDB getInstancia() {
        if(instancia == null)
            instancia = new RedisDB();
        return instancia;
    }

    public Jedis getConnection() throws RedisConnectionException {
        try {
            Jedis jedis = pool.getResource();
            return jedis;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RedisConnectionException("Error al conectarme a Redis");
        }
    }
}
