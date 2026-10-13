package com.hotel.Hotel.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * NO puede ser un record: Hibernate 6 ordena los atributos de un embeddable
 * alfabeticamente, asi que al materializar invocaba el constructor canonico con los
 * argumentos invertidos (fechaFin, fechaInicio) y la validacion de dominio lo rechazaba
 * con "La fecha fin no puede ser anterior a la de inicio". Una clase con constructor
 * no-arg y acceso por campos elimina esa ambiguedad: el orden ya no importa.
 *
 * <p>Sigue siendo inmutable desde Java: no hay setters publicos.
 */
@Embeddable
public class RangoFechas {

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    protected RangoFechas() {
    }

    public RangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha fin no puede ser anterior a la de inicio");
        }
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public long getDias() {
        return ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }
}
