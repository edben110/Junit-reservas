package com.hotel.Hotel.dto.response;

import com.hotel.Hotel.domain.EstadoHabitacion;
import com.hotel.Hotel.domain.TipoHabitacion;

import java.util.UUID;

/**
 * Contrato cerrado de la proyección de una habitación.
 *
 * <p>Al ser {@code sealed}, el compilador garantiza que solo existan las dos
 * implementaciones declaradas en {@code permits}: agregar un tercer tipo obliga a
 * modificar esta lista de forma explícita.
 */
public sealed interface HabitacionResponse
        permits HabitacionEstandarResponse, SuitePresidencialResponse {

    UUID id();

    String numero();

    int capacidadMaxima();

    double precioPorNoche();

    EstadoHabitacion estado();

    /**
     * Discriminador de tipo. Viaja como componente del record (no como método suelto)
     * porque Jackson solo serializa a JSON los componentes de un record o los métodos con
     * naming {@code getX}/{@code isX}; un {@code tipo()} aislado no aparecería en la respuesta.
     */
    TipoHabitacion tipo();
}
