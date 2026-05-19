package com.pasteleria.ms_auth.exception;



public class EmailDuplicadoException extends RuntimeException {

    public EmailDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
