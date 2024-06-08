package org.uade.services;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
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
import java.util.Scanner;

public class RedisService {
    private final Jedis database;
    private LocalDateTime tiempoInicio;
    private MongoService mongoService;
    Scanner sc = new Scanner(System.in);


    public RedisService(MongoService mongoService) throws RedisConnectionException{
        this.mongoService = mongoService;
        this.database = RedisDB.getInstancia().getConnection();
    }

    // Clase que se utiliza para ver la hora de inicio de sesion del usuario.
    public void iniciarSesion(String idUsuario) {
        // en un futuro podemos guardar la fecha de inicio de sesion y chequear si es vacio, el mismo dia u otro dia
        tiempoInicio = LocalDateTime.now();
        this.database.hset("usuario:" + idUsuario, "Inicio", String.valueOf(tiempoInicio)); // Este podria borrarse y colocarse en el cerrarSesion.
        System.out.println("Sesión iniciada con éxito!");
    }

    // Clase que se utiliza para ver la hora de cerrado de sesion del usuario.
    public void cerrarSesion(String idUsuario) {

        Duration duration = Duration.between(tiempoInicio, LocalDateTime.now());

        long minutos = duration.toMinutes();

        if (minutos >= 240)
            this.database.hset("usuario:" + idUsuario, "Categorización", "TOP");
        else if (minutos >= 120)
            this.database.hset("usuario:" + idUsuario, "Categorización", "MEDIUM");
        else
            this.database.hset("usuario:" + idUsuario, "Categorización", "LOW");

        System.out.println("Sesión cerrada con éxito!");
    }

    public void agregarProductoCarrito(String idUsuario) {
        mostrarCarrito(idUsuario);

        System.out.print("Ingrese el id del producto a comprar: ");
        int idProducto = sc.nextInt();
        Producto producto = mongoService.recuperarProducto(Filters.eq("idProducto", idProducto));
        while (producto != null) {
            System.out.printf("El producto con id: %s no existe. Vuelva a ingresar el id del producto: ", idProducto);
            idProducto = sc.nextInt();
            producto = mongoService.recuperarProducto(Filters.eq("idProducto", idProducto));
        }

        System.out.print("Ingrese la cantidad que desea agregar: ");
        int cantidad = sc.nextInt();
        while (cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor a 0");
            System.out.print("Ingrese la cantidad que desea agregar: ");
            cantidad = sc.nextInt();
        }
        this.database.hset("carrito:" + idUsuario, String.valueOf(idProducto), String.valueOf(cantidad));
    }

    public void eliminarProducto(String idUsuario) {
        mostrarCarrito(idUsuario);

        System.out.print("Ingrese el id del producto a eliminar del carrito: ");
        int idProducto = sc.nextInt();
        Producto producto = mongoService.recuperarProducto(Filters.eq("idProducto", idProducto));
        while (producto != null) {
            System.out.printf("El producto con id: %s no existe. Vuelva a ingresar el id del producto: ", idProducto);
            idProducto = sc.nextInt();
            producto = mongoService.recuperarProducto(Filters.eq("idProducto", idProducto));
        }

        this.database.hdel("carrito:" + idUsuario, String.valueOf(idProducto));
    }

    public void modificarCantidadProductoCarrito(String idUsuario) {
        mostrarCarrito(idUsuario);

        System.out.print("Ingrese el id del producto a modificar cantidad: ");
        int idProducto = sc.nextInt();
        Producto producto = mongoService.recuperarProducto(Filters.eq("idProducto", idProducto));
        while (producto != null) {
            System.out.printf("El producto con id: %s no existe. Vuelva a ingresar el id del producto: ", idProducto);
            idProducto = sc.nextInt();
            producto = mongoService.recuperarProducto(Filters.eq("idProducto", idProducto));
        }

        System.out.print("Ingrese la nueva cantidad del producto: ");
        int cantidad = sc.nextInt();
        while (cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor a 0");
            System.out.print("Ingrese la cantidad que desea agregar: ");
            cantidad = sc.nextInt();
        }
        this.database.hset("carrito:" + idUsuario, String.valueOf(idProducto), String.valueOf(cantidad));
    }

    public Map<Producto, Integer> recuperarCarrito(String idUsuario) {
        Map<String, String> carritoUsuario = this.database.hgetAll("carrito:" + idUsuario);

        Map<Producto, Integer> itemsCarrito = new HashMap<>();

        for (Map.Entry<String, String> entry : carritoUsuario.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
            int idProducto = Integer.parseInt(entry.getKey());
            Bson filter = Filters.eq("idProducto", idProducto);
            Producto producto = mongoService.recuperarProducto(filter);
            Integer cantidad = Integer.valueOf(entry.getValue());
            itemsCarrito.put(producto, cantidad);
        }
        return itemsCarrito;
    }

    private void mostrarCarrito(String idUsuario) {
        Map<Producto, Integer> itemsCarrito = recuperarCarrito(idUsuario);
        int aux = 0;
        for (Map.Entry<Producto, Integer> entry : itemsCarrito.entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();
            aux++;
            System.out.printf("%d. Producto: %s - Cantidad: %d\n", aux, producto, cantidad);
        }
    }
}
