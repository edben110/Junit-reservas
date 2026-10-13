package com.hotel.Hotel.dto.response;

import com.hotel.Hotel.domain.EstadoHabitacion;
import com.hotel.Hotel.domain.TipoHabitacion;

import java.util.UUID;

public record HabitacionEstandarResponse(
        UUID id,
        String numero,
        int capacidadMaxima,
        double precioPorNoche,
        EstadoHabitacion estado,
        TipoHabitacion tipo,
        int camasIndividuales) implements HabitacionResponse {
}
