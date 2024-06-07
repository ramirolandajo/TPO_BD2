package org.uade;

import org.uade.exceptions.MongoConnectionException;
import org.uade.exceptions.RedisConnectionException;
import org.uade.models.Usuario;
import org.uade.services.MongoService;
import org.uade.services.RedisService;

import java.util.Scanner;
import java.util.Stack;

public class App {

    public static void main( String[] args ) throws MongoConnectionException, RedisConnectionException {
        MongoService mongo = new MongoService();
        RedisService redis = new RedisService();

        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenido a tu tienda de deporte preferida!");
        System.out.println("Ingresa una opción:");

        System.out.println("1. Registrarse a la aplicación (si ingresas por primera vez)");
        System.out.println("2. Ingresar a la aplicación");
        System.out.println("3 Salir");
        int opcion = sc.nextInt();

        while (opcion != 1 && opcion != 2 && opcion != 3 && opcion != 4) {
            System.out.println("La opción ingresada no es válida");
            opcion = sc.nextInt();
        }

        if  (opcion == 1) {
            System.out.print("Ingrese su nombre completo: ");
            String nombreCompleto = sc.nextLine();

            System.out.print("Ingrese su documento: ");
            String documento = sc.nextLine();

            System.out.print("Ingrese su dirección: ");
            String direccion = sc.nextLine();

            System.out.print("Ingrese su saldo: ");
            Float cuentaCorriente = sc.nextFloat();

            Usuario usuario = mongo.recuperarUsuario(documento);
            if(usuario != null){
                System.out.println("El usuario con DNI " + documento + " ya se encuentra registrado en la base de datos!");
            }else{
                mongo.agregarUsuario(new Usuario(documento,nombreCompleto,direccion,cuentaCorriente));
            }
        }
        else if (opcion == 2){
            System.out.print("Ingrese su DNI: ");
            int doc = sc.nextInt();
            while(doc < 1) {
                System.out.println("Número no válido. Ingrese un número positivo!");
                doc = sc.nextInt();
            }if(doc == 1){
                System.out.println("1.- Ver productos");
                System.out.println("2.- Modificar producto");
                System.out.println("3.- Agregar producto al catálogo");
                System.out.println("4.- Ver log de cambios del catálogo");
                System.out.println("5.- Ver las facturas");
                System.out.println("0.- SALIR");
                System.out.print("Ingrese una opción: ");
                int opcionAdmin = sc.nextInt();

                switch (opcionAdmin){
                    case 1:
                        mongo.recuperarCatalogo();
                    case 2:
                        System.out.print("Ingrese el producto que desea actualizar: ");
                        int idProducto = sc.nextInt();
                        mongo.modificarProducto(idProducto);
                    case 3:
                        mongo.agregarProductoAlCatalogo();
                    case 4:

                }
            }
            else{
                System.out.println();
            }
        }
        else{
            System.out.println();
        }
    }
}
