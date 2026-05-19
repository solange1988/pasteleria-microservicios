package com.pasteleria.ms_pagos.exception;



public class PagoInvalidoException extends RuntimeException{
    public PagoInvalidoException(String mensaje) {
        super(mensaje);
    }

}
