package com.pasteleria.ms_inventario.exception;

public class StockInsuficienteException extends RuntimeException{

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
