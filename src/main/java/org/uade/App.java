package org.uade;

import org.uade.models.Usuario;
import org.uade.services.CassandraService;
import org.uade.services.MongoService;
import org.uade.services.RedisService;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class App {

    public static MongoService mongoService;
    public static CassandraService cassandraService;
    public static RedisService redisService;

    public static void main(String[] args) {
        try {
            mongoService = new MongoService(null, null);
            cassandraService = new CassandraService(mongoService);
            redisService = new RedisService(mongoService);

            // After creating the redisService, set it in mongoService and cassandraService
            mongoService = new MongoService(cassandraService, redisService);

            // Now all services are properly wired and can interact with each other
            // Your application logic here

        } catch (Exception e) {
            e.printStackTrace();
        }

        Scanner sc = new Scanner(System.in).useLocale(Locale.US);
        System.out.println("Bienvenido a tu tienda de deporte preferida!");
        System.out.println("Ingresa una opción:");

        System.out.println("1. Registrarse a la aplicación (si ingresas por primera vez)");
        System.out.println("2. Ingresar a la aplicación");
        System.out.println("3. Salir de la aplicacion");
        int opcion = sc.nextInt();
        sc.nextLine();

        while (opcion != 1 && opcion != 2 && opcion != 3) {
            System.out.println("La opción ingresada no es válida");
            opcion = sc.nextInt();
        }

        while (opcion != 3) {
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
                Usuario usuario = mongoService.recuperarUsuario(documento);
                if (usuario != null) {
                    System.out.println("El usuario con DNI " + documento + " ya se encuentra registrado en la base de datos!");
                } else {
                    mongoService.agregarUsuario(new Usuario(documento, nombreCompleto, direccion, cuentaCorriente, tipoUsuario));
                }

            } else if (opcion == 2) {
                System.out.print("Ingrese su DNI: ");
                int doc = sc.nextInt();
                while (doc < 1) {
                    System.out.println("Número no válido. Ingrese un número positivo!");
                    doc = sc.nextInt();

                }
                // Opciones Administrador
                if (doc == 1) {
                    System.out.println("Bienvenido al menú de Administrador!");
                    System.out.println("\n1.- Ver productos");
                    System.out.println("2.- Modificar producto");
                    System.out.println("3.- Agregar producto al catálogo");
                    System.out.println("4.- Ver log de cambios del catálogo");
                    System.out.println("5.- Ver log de facturas");
                    // System.out.println("Ver actividad diaria de usuario");
                    System.out.println("0.- SALIR");

                    System.out.print("\nIngrese una opción: ");
                    int opcionAdmin = sc.nextInt();

                    while (opcionAdmin != 0) {
                        switch (opcionAdmin) {
                            case 1:
                                mongoService.recuperarCatalogo();
                                sc.nextLine();
                                sc.nextLine();
                                break;
                            case 2:
                                System.out.print("Ingrese el producto que desea actualizar: ");
                                int idProducto = sc.nextInt();
                                mongoService.actualizarProducto(idProducto);
                                break;
                            case 3:
                                mongoService.agregarProductoAlCatalogo();
                                break;
                            case 4:
                                cassandraService.verLogsCatalogo();
                                break;
                            case 5:
                                cassandraService.verLogFacturas();
                        }
                        System.out.println("Bienvenido al menú de Administrador!");
                        System.out.println("\n1.- Ver productos");
                        System.out.println("2.- Modificar producto");
                        System.out.println("3.- Agregar producto al catálogo");
                        System.out.println("4.- Ver log de cambios del catálogo");
                        System.out.println("5.- Ver log de facturas");
                        // System.out.println("Ver actividad diaria de usuario");
                        System.out.println("0.- SALIR");

                        System.out.print("\nIngrese una opción: ");
                        opcionAdmin = sc.nextInt();
                    }
                }
                // Opciones usuario comun
                else {
                    redisService.iniciarSesion(String.valueOf(doc));
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

                    List<Integer> opciones = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 0);
                    while (opcionCliente != 0) {
                        while (!opciones.contains(opcionCliente)) {
                            System.out.print("Opcion no valida. Vuelva a intentar: ");
                            opcionCliente = sc.nextInt();
                        }
                        switch (opcionCliente) {
                            case 1:
                                mongoService.recuperarCatalogo();
                                break;
                            case 2:
                                redisService.agregarProductoCarrito(String.valueOf(doc));
                                break;
                            case 3:
                                redisService.eliminarProductoCarrito(String.valueOf(doc));
                                break;
                            case 4:
                                redisService.modificarCantidadProductoCarrito(String.valueOf(doc));
                                break;
                            case 5:
                                System.out.println();
                                break;
                            case 6:
                                mongoService.recuperarFacturasUsuario(String.valueOf(doc));
                                break;
                            case 7:
                                mongoService.pagarFactura();
                                break;
                            case 0:
                                redisService.cerrarSesion(String.valueOf(doc));
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
            } else {
                System.out.println();
            }
            System.out.println("1. Registrarse a la aplicación (si ingresas por primera vez)");
            System.out.println("2. Ingresar a la aplicación");
            System.out.println("3. Salir de la aplicacion");
            opcion = sc.nextInt();
        }
        cassandraService.close();
        mongoService.close();
        redisService.close();
    }
}