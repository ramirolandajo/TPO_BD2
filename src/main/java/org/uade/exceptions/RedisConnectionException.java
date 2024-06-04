package org.uade.exceptions;

public class RedisConnectionException extends Exception {

    private static final long serialVersionUID = 601128023082493198L;

    public RedisConnectionException(String mensaje) {
        super(mensaje);
    }

}
