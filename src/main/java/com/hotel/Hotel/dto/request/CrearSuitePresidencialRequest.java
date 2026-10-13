package com.hotel.Hotel.dto.request;

public record CrearSuitePresidencialRequest(
        String numero,
        int capacidadMaxima,
        double precioPorNoche,
        boolean incluyeMayordomo,
        boolean jacuzziPrivado) {
}
