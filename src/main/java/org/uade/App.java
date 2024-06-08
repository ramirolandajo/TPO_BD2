package org.uade;

import org.uade.exceptions.CassandraConnectionException;
import org.uade.exceptions.MongoConnectionException;
import org.uade.exceptions.RedisConnectionException;
import org.uade.models.Usuario;
import org.uade.services.CassandraService;
import org.uade.services.MongoService;
import org.uade.services.RedisService;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) throws MongoConnectionException, RedisConnectionException, CassandraConnectionException {
        MongoService mongo = new MongoService();
        RedisService redis = new RedisService();
        CassandraService cassandra = new CassandraService();

        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenido a tu tienda de deporte preferida!");
        System.out.println("Ingresa una opción:");

        System.out.println("1. Registrarse a la aplicación (si ingresas por primera vez)");
        System.out.println("2. Ingresar a la aplicación");
        System.out.println("3. Salir");
        int opcion = sc.nextInt();

        while (opcion != 1 && opcion != 2 && opcion != 3 && opcion != 4) {
            System.out.println("La opción ingresada no es válida");
            opcion = sc.nextInt();
        }

        if (opcion == 1) {
            System.out.print("Ingrese su nombre completo: ");
            String nombreCompleto = sc.nextLine();

            System.out.print("Ingrese su documento: ");
            String documento = sc.nextLine();

            System.out.print("Ingrese su dirección: ");
            String direccion = sc.nextLine();

            System.out.print("Ingrese su saldo: ");
            Float cuentaCorriente = sc.nextFloat();

            System.out.println("Ingrese 1. si es EMPRESA");
            System.out.println("Ingrese 2. si es INDIVIDUO");
            int tipoUsuarioInt = sc.nextInt();
            String tipoUsuario;
            while (tipoUsuarioInt != 1 && tipoUsuarioInt != 2) {
                System.out.println("Opcion no valida. Ingrese una opcion valida.");
                tipoUsuarioInt = sc.nextInt();
            }
            if (tipoUsuarioInt == 1) {
                tipoUsuario = "EMPRESA";
            } else {
                tipoUsuario = "INDIVIDUO";
            }
            Usuario usuario = mongo.recuperarUsuario(documento);
            if (usuario != null) {
                System.out.println("El usuario con DNI " + documento + " ya se encuentra registrado en la base de datos!");
            } else {
                mongo.agregarUsuario(new Usuario(documento, nombreCompleto, direccion, cuentaCorriente, tipoUsuario));
            }

        }
        // Opciones Administrador
        else if (opcion == 2) {
            System.out.print("Ingrese su DNI: ");
            String doc = sc.nextLine();
            while (Integer.parseInt(doc) < 1) {
                System.out.println("Número no válido. Ingrese un número positivo!");
                doc = sc.nextLine();

            }
            if (Integer.parseInt(doc) == 1) {
                System.out.println("Bienvenido al menú de Administrador!");
                System.out.println("\n1.- Ver productos");
                System.out.println("2.- Modificar producto");
                System.out.println("3.- Agregar producto al catálogo");
                System.out.println("4.- Ver log de cambios del catálogo");
                System.out.println("5.- Ver log de facturas");
                System.out.println("0.- SALIR");

                System.out.print("\nIngrese una opción: ");
                int opcionAdmin = sc.nextInt();

                switch (opcionAdmin) {
                    case 1:
                        mongo.recuperarCatalogo();
                    case 2:
                        System.out.print("Ingrese el producto que desea actualizar: ");
                        int idProducto = sc.nextInt();
                        mongo.actualizarProducto(idProducto);
                    case 3:
                        mongo.agregarProductoAlCatalogo();
                    case 4:
                        cassandra.verLogsCatalogo();
                    case 5:
                        cassandra.verLogFacturas();
                    case 0:
                        break;
                }
            }
            // Opciones usuario comun
            else {
                redis.iniciarSesion(doc);
                System.out.println("Bienvenido al menú de Cliente");
                System.out.println("\n1.- Ver productos");
                System.out.println("2.- Agregar producto al carrito");
                System.out.println("3.- Eliminar producto del carrito");
                System.out.println("4.- Modificar cantidad producto del carrito");
                System.out.println("5.- Confirmar carrito (generar pedido)");
                System.out.println("6.- Ver facturas");
                System.out.println("7.- Pagar factura");
                System.out.println("0.- SALIR");

                System.out.print("\nIngrese una opción: ");
                int opcionCliente = sc.nextInt();

                List<Integer> opciones = Arrays.asList(1,2,3,4,5,6,7,0);
                while (opcionCliente != 0) {
                    while (!opciones.contains(opcionCliente)) {
                        System.out.print("Opcion no valida. Vuelva a intentar: ");
                        opcionCliente = sc.nextInt();
                    }
                    switch (opcionCliente) {
                        case 1:
                            mongo.recuperarCatalogo();
                            break;
                        case 2:
                            //TODO
                            System.out.println();
                            break;
                        case 3:
                            //TODO
                            System.out.println();
                            break;
                        case 4:
                            //TODO
                            System.out.println();
                            break;
                        case 5:
                            //TODO
                            System.out.println();
                            break;
                        case 6:
                            mongo.recuperarFacturasUsuario(doc);
                            break;
                        case 7:
                            //TODO
                            System.out.println();
                            break;
                        case 0:
                            redis.cerrarSesion(doc);
                            break;
                        default:
                            break;
                    }
                    System.out.println("\n1.- Ver productos");
                    System.out.println("2.- Agregar producto al carrito");
                    System.out.println("3.- Eliminar producto del carrito");
                    System.out.println("4.- Modificar cantidad producto del carrito");
                    System.out.println("5.- Confirmar carrito (generar pedido)");
                    System.out.println("6.- Ver facturas");
                    System.out.println("7.- Pagar factura");
                    System.out.println("0.- SALIR");

                    System.out.print("\nIngrese una opción: ");
                    opcionCliente = sc.nextInt();
                }
            }
        } else
            System.out.println();
    }
}