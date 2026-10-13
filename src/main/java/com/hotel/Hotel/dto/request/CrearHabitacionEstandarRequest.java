package com.hotel.Hotel.dto.request;

public record CrearHabitacionEstandarRequest(
        String numero,
        int capacidadMaxima,
        double precioPorNoche,
        int camasIndividuales) {
}
