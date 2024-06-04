package org.uade.exceptions;

public class ErrorConectionMongoException extends Exception {

    private static final long serialVersionUID = 601128023082493198L;

    public ErrorConectionMongoException(String mensaje) {
        super(mensaje);
    }

}