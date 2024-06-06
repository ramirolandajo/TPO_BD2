package org.uade;

import org.uade.exceptions.MongoConnectionException;
import org.uade.models.Carrito;
import org.uade.models.Usuario;
import org.uade.services.MongoService;

import java.util.List;
import java.util.Scanner;

public class App {

    static MongoService mongo;

    static { // Controla excepciones al inicializar un atributo estático, en este caso la instancia de Mongo.
        try {
            mongo = MongoService.getInstancia();
        } catch (MongoConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main( String[] args ) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenido a tu tienda de deporte preferida!");
        System.out.println("Ingresa una opción:");

        System.out.println("1. Registrarse a la aplicación (si ingresas por primera vez)");
        System.out.println("2. Ingresar a la aplicación");
        System.out.println("3. Ingreso administrador");
        System.out.println("4. Salir");
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
            //metodos de usuario
        }
        else if (opcion == 3){
            //metodos admin
        }
        else {
            //exit
        }
    }
}
