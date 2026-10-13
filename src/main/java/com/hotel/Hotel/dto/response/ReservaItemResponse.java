package com.hotel.Hotel.dto.response;

import com.hotel.Hotel.domain.EstadoReserva;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Sub-DTO de la vista de recepción. Deliberadamente NO vuelve a referenciar al cliente:
 * esa ausencia de referencia hacia atrás es lo que corta el ciclo de serialización
 * Cliente -> Reserva -> Cliente -> ... que de otro modo sería infinito.
 */
public record ReservaItemResponse(
        UUID idReserva,
        String numeroHabitacion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        EstadoReserva estado,
        double costoTotal) {
}
