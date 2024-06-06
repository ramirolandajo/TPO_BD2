package org.uade;

import java.util.Scanner;

public class App 
{
    public static void main( String[] args ) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenido a tu tienda de deporte preferida!");
        System.out.println("Ingresa una opcion:");
        // hacemos que el admin cree el usuario con los datos,
        // o le metemos este registrarse sin chequear mucho? (capaz podemos chequear,
        // que el dni del usuario no se encuentre en la bd y listo)
        System.out.println("1. Registrarse a la aplicacion (si ingresas por primera vez)");
        System.out.println("2. Ingresar a la aplicacion");
        System.out.println("3. Ingreso administrador");
        System.out.println("4. Salir");
        int opcion = sc.nextInt();
        while (opcion != 1 && opcion != 2 && opcion != 3 && opcion != 4) {
            System.out.println("La opcion ingresada no es valida");
            opcion = sc.nextInt();
        }

        if  (opcion == 1) {
            // metodos de registro y luego metodos de usuario

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
