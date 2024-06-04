package org.uade.exceptions;

public class MongoConnectionException extends Exception {

    private static final long serialVersionUID = 601128023082493198L;

    public MongoConnectionException(String mensaje) {
        super(mensaje);
    }

}