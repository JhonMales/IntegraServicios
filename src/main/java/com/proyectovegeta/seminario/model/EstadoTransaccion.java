package com.proyectovegeta.seminario.model;

public enum EstadoTransaccion {
    activa("activa"),
    cancelada("cancelada"),
    completada("completada");
    private String value;

    EstadoTransaccion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
