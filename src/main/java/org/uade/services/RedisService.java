package org.uade.services;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.uade.connections.RedisDB;
import org.uade.exceptions.RedisConnectionException;
import org.uade.models.Producto;
import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class RedisService {
    private final Jedis database;
    private LocalDateTime tiempoInicio;
    private MongoService mongoService;
    Scanner sc = new Scanner(System.in).useLocale(Locale.US);
    Stack<Map<String, String>> estadoCarrito;


    public RedisService(MongoService mongoService) throws RedisConnectionException {
        this.mongoService = mongoService;
        this.database = RedisDB.getInstancia().getConnection();
    }

    // Método que se utiliza para ver la hora de inicio de sesion del usuario.
    public void iniciarSesion(String idUsuario) {
        // en un futuro podemos guardar la fecha de inicio de sesion y chequear si es vacio, el mismo dia u otro dia
        tiempoInicio = LocalDateTime.now();

        this.database.hset("usuario:" + idUsuario, "Inicio", String.valueOf(tiempoInicio));
        estadoCarrito = new Stack<>();

        System.out.println("Sesión iniciada con éxito!");
    }

    // Método que se utiliza para ver la hora de cerrado de sesion del usuario.
    public void cerrarSesion(String idUsuario) {

        Duration duration = Duration.between(tiempoInicio, LocalDateTime.now());
        long minutos = duration.toMinutes();

        if (minutos >= 240)
            this.database.hset("usuario:" + idUsuario, "categorizacion", "TOP");
        else if (minutos >= 120)
            this.database.hset("usuario:" + idUsuario, "categorizacion", "MEDIUM");
        else
            this.database.hset("usuario:" + idUsuario, "categorizacion", "LOW");

        // vaciamos el carrito y lo eliminamos de la bd antes de cerrar sesion
        estadoCarrito.empty();
        this.database.del("carrito:" + idUsuario);
        System.out.println("Sesión cerrada con éxito!");
    }

    public void verActividadUsuario(){
        System.out.print("Ingrese el usuario que desea ver: ");
        String idUsuario = sc.nextLine();

        while ((Integer.parseInt(idUsuario) != -1) && (mongoService.recuperarUsuario(idUsuario) == null)) {
            System.out.print("El usurio con el documento " + idUsuario + " no existe! Intente de nuevo: ");
            idUsuario = sc.nextLine();
        }

        if(Integer.parseInt(idUsuario) != -1) {
            System.out.println("Actividad del usurio: " + this.database.hget("usuario:" + idUsuario, "categorizacion"));
            sc.nextLine();
        }
    }

    public void agregarProductoCarrito(String idUsuario) {
        mostrarCarrito(idUsuario);

        System.out.print("Ingrese el id del producto a comprar: ");
        int idProducto = sc.nextInt();
        sc.nextLine();
        Producto producto = mongoService.recuperarProducto(idProducto);
        while (producto == null) {
            System.out.printf("El producto con id: %d no existe. Vuelva a ingresar el id del producto: ", idProducto);
            idProducto = sc.nextInt();
            sc.nextLine();
            producto = mongoService.recuperarProducto(idProducto);
        }

        System.out.print("Ingrese la cantidad que desea agregar: ");
        int cantidad = sc.nextInt();
        sc.nextLine();

        while (cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor a 0");
            System.out.print("Ingrese la cantidad que desea agregar: ");
            cantidad = sc.nextInt();
            sc.nextLine();
        }
        this.database.hset("carrito:" + idUsuario, String.valueOf(idProducto), String.valueOf(cantidad));

        // Recuperamos el carrito primitivo (string, string) y guardamos su estado
        Map<String, String> carritoUsuario = this.database.hgetAll("carrito:" + idUsuario);
        if (carritoUsuario != null) {
            this.estadoCarrito.push(carritoUsuario);
        } else
            this.estadoCarrito.empty();

        System.out.printf("Producto con id: %d agregado al carrito!\n", idProducto);
    }

    public void eliminarProductoCarrito(String idUsuario) {
        mostrarCarrito(idUsuario);

        System.out.print("Ingrese el id del producto a eliminar del carrito: ");
        int idProducto = sc.nextInt();

        Map<Producto, Integer> carritoActual = recuperarCarrito(idUsuario);
        boolean productoEnCarrito = false;
        for (Map.Entry<Producto, Integer> entry : carritoActual.entrySet()) {
            int idProductoCarrito = entry.getKey().getIdProducto();
            if (idProducto == idProductoCarrito) {
                productoEnCarrito = true;
                break;
            }
        }

        if (!productoEnCarrito) {
            System.out.println("El producto con id: " + idProducto + " no se encuentra en el carrito/no existe");
            return;
        }

        this.database.hdel("carrito:" + idUsuario, String.valueOf(idProducto));

        // Recuperamos el carrito primitivo (string, string) y guardamos su estado
        Map<String, String> carritoUsuario = this.database.hgetAll("carrito:" + idUsuario);
        if (carritoUsuario != null) {
            this.estadoCarrito.push(carritoUsuario);
        } else
            this.estadoCarrito.empty();

        System.out.printf("Producto con id: %d eliminado del carrito!\n", idProducto);
    }

    public void modificarCantidadProductoCarrito(String idUsuario) {
        mostrarCarrito(idUsuario);

        System.out.print("Ingrese el id del producto a modificar cantidad: ");
        int idProducto = sc.nextInt();
        Map<Producto, Integer> carritoActual = recuperarCarrito(idUsuario);
        boolean productoEnCarrito = false;
        for (Map.Entry<Producto, Integer> entry : carritoActual.entrySet()) {
            int idProductoCarrito = entry.getKey().getIdProducto();
            if (idProducto == idProductoCarrito) {
                productoEnCarrito = true;
                break;
            }
        }

        if (!productoEnCarrito) {
            System.out.println("El producto con id: " + idProducto + " no se encuentra en el carrito/no existe");
            return;
        }

        System.out.print("Ingrese la nueva cantidad del producto: ");
        int cantidad = sc.nextInt();
        while (cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor a 0");
            System.out.print("Ingrese la cantidad que desea agregar: ");
            cantidad = sc.nextInt();
            sc.nextLine();
        }
        this.database.hset("carrito:" + idUsuario, String.valueOf(idProducto), String.valueOf(cantidad));

        // Recuperamos el carrito primitivo (string, string) y guardamos su estado
        Map<String, String> carritoUsuario = this.database.hgetAll("carrito:" + idUsuario);
        if (carritoUsuario != null) {
            this.estadoCarrito.push(carritoUsuario);
        } else
            this.estadoCarrito.empty();

        System.out.printf("Se ha modificado la cantidad del producto con id: %d. Nueva cantidad: %d!\n", idProducto, cantidad);
    }

    public Map<Producto, Integer> recuperarCarrito(String idUsuario) {
        Map<String, String> carritoUsuario = this.database.hgetAll("carrito:" + idUsuario);
        if (carritoUsuario != null && !carritoUsuario.isEmpty()) {
            Map<Producto, Integer> itemsCarrito = new HashMap<>();

            for (Map.Entry<String, String> entry : carritoUsuario.entrySet()) {
                int idProducto = Integer.parseInt(entry.getKey());

                Producto producto = mongoService.recuperarProducto(idProducto);
                Integer cantidad = Integer.valueOf(entry.getValue());

                itemsCarrito.put(producto, cantidad);
            }
            return itemsCarrito;
        }
        return null;
    }

    public void mostrarCarrito(String idUsuario) {
        Map<Producto, Integer> itemsCarrito = recuperarCarrito(idUsuario);
        if (itemsCarrito != null) {
            int aux = 0;
            for (Map.Entry<Producto, Integer> entry : itemsCarrito.entrySet()) {
                Producto producto = entry.getKey();
                int cantidad = entry.getValue();
                aux++;
                System.out.printf("%d. Producto: %s - Descripcion: %s - Cantidad: %d\n",
                        aux, producto.getIdProducto(), producto.getDescripcion(), cantidad);
            }
            System.out.println("Enter para continuar...");
            sc.nextLine();
        } else
            System.out.println("No hay productos en el carrito.");
    }

    public void vaciarCarrito(String idUsuario) {
        this.estadoCarrito.empty();
        this.database.del("carrito:" + idUsuario);
    }

    public void undoEstadoCarrito(String idUsuario) {
        if (!this.estadoCarrito.isEmpty()) {
            // Eliminamos el ultimo estado del stack
            estadoCarrito.pop();

            // Recuperamos el estado actual sin sacarlo del carrito
            if (!this.estadoCarrito.isEmpty()) {
                Map<String, String> estadoActualCarrito = estadoCarrito.peek();
                this.database.del("carrito:" + idUsuario);
                for (Map.Entry<String, String> entry : estadoActualCarrito.entrySet()) {
                    String idProducto = entry.getKey();
                    String cantidad = entry.getValue();
                    this.database.hset("carrito:" + idUsuario, idProducto, cantidad);
                }
                System.out.println("Vuelto al estado anterior del carrito.");

            } else {
                this.database.del("carrito:" + idUsuario);
                System.out.println("Carrito vaciado.");
            }
        } else
            System.out.println("El carrito esta vacio");
    }

    public void close() {
        this.database.close();
        RedisDB.getInstancia().closeConnection();
    }
}