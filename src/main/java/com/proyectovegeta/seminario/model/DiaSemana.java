package com.proyectovegeta.seminario.model;

public enum DiaSemana {
    lunes("lunes"),
    martes("martes"),
    miercoles("miercoles"),
    jueves("jueves"),
    viernes("viernes"),
    sabado("sabado"),
    domingo("domingo");

    private String value;

    DiaSemana(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}