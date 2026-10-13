package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.Cliente;
import com.hotel.Hotel.domain.Reserva;
import com.hotel.Hotel.dto.request.ActualizarClienteRequest;
import com.hotel.Hotel.dto.request.CrearClienteRequest;
import com.hotel.Hotel.dto.response.ClienteResponse;
import com.hotel.Hotel.dto.response.ClienteResumenResponse;
import com.hotel.Hotel.dto.response.ReservaItemResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteResponse toResponse(Cliente cliente);

    List<ClienteResponse> toResponseList(List<Cliente> clientes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "penalizaciones", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    Cliente toEntity(CrearClienteRequest request);

    /**
     * Proyeccion plana de una reserva. Se cortan aqui las referencias inversas
     * (reserva -> cliente, reserva -> habitacion) para que el grafo JSON sea finito.
     */
    @Mapping(target = "idReserva", source = "id")
    @Mapping(target = "numeroHabitacion", expression = "java(reserva.getHabitacion().getNumero())")
    @Mapping(target = "fechaInicio", expression = "java(reserva.getPeriodo().getFechaInicio())")
    @Mapping(target = "fechaFin", expression = "java(reserva.getPeriodo().getFechaFin())")
    ReservaItemResponse toReservaItem(Reserva reserva);

    @Named("reservasRecientes")
    default List<ReservaItemResponse> reservasRecientes(List<Reserva> reservas) {
        return reservas.stream().map(this::toReservaItem).toList();
    }

    @Mapping(target = "totalReservasRealizadas", expression = "java(cliente.getReservas().size())")
    @Mapping(target = "montoTotalGastado",
            expression = "java(cliente.getReservas().stream().mapToDouble(r -> r.getCostoTotal()).sum())")
    @Mapping(target = "reservasRecientes", source = "reservas", qualifiedByName = "reservasRecientes")
    ClienteResumenResponse toResumen(Cliente cliente);

    /**
     * Actualizacion parcial: NullValuePropertyMappingStrategy.IGNORE hace que el
     * procesador wrappee cada asignacion en un {@code if (dto.campo() != null)}, de modo
     * que un null significa "no tocar" y no "sobrescribir con null".
     *
     * <p>Los ignores explicitos son la segunda linea de defensa: Cliente no expone setter
     * para id, activo ni penalizaciones, asi que el compilador ya impide el over-assignment.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "penalizaciones", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    void updateClienteFromDto(ActualizarClienteRequest dto, @MappingTarget Cliente entity);
}
