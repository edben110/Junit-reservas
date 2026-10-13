package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.Habitacion;
import com.hotel.Hotel.domain.HabitacionEstandar;
import com.hotel.Hotel.domain.SuitePresidencial;
import com.hotel.Hotel.domain.TipoHabitacion;
import com.hotel.Hotel.dto.response.HabitacionEstandarResponse;
import com.hotel.Hotel.dto.response.HabitacionResponse;
import com.hotel.Hotel.dto.response.SuitePresidencialResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HabitacionMapper {

    /**
     * Proyección polimórfica. MapStruct 1.5.5 no genera mapeo automático hacia una
     * interfaz sellada (eso llega en 1.6 con {@code @SubtypeMapping}), así que el despacho
     * por subclase concreta se resuelve aquí con pattern matching de Java 17 y se delega
     * en los dos métodos de abajo, que sí genera el procesador de anotaciones.
     *
     * <p>Al ser un {@code default}, MapStruct no lo sobrescribe: se conserva tal cual en
     * la implementación generada.
     */
    default HabitacionResponse toResponse(Habitacion habitacion) {
        if (habitacion instanceof HabitacionEstandar estandar) {
            return toEstandarResponse(estandar);
        }
        if (habitacion instanceof SuitePresidencial suite) {
            return toSuiteResponse(suite);
        }
        throw new IllegalArgumentException(
                "Tipo de habitación no soportado: " + habitacion.getClass().getSimpleName());
    }

    @Mapping(target = "tipo", expression = "java(TipoHabitacion.ESTANDAR)")
    HabitacionEstandarResponse toEstandarResponse(HabitacionEstandar habitacion);

    @Mapping(target = "tipo", expression = "java(TipoHabitacion.SUITE)")
    SuitePresidencialResponse toSuiteResponse(SuitePresidencial habitacion);

    default List<HabitacionResponse> toResponseList(List<Habitacion> habitaciones) {
        return habitaciones.stream().map(this::toResponse).toList();
    }
}
