package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.Cliente;
import com.hotel.Hotel.domain.HabitacionEstandar;
import com.hotel.Hotel.domain.RangoFechas;
import com.hotel.Hotel.domain.Reserva;
import com.hotel.Hotel.dto.response.ReservaResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReservaMapperTest {

    private final ReservaMapper mapper = new ReservaMapperImpl();

    @Test
    void toResponse_mapeaIdEstadoYCostoTotal() {
        Reserva reserva = nuevaReserva();

        ReservaResponse response = mapper.toResponse(reserva);

        assertThat(response.id()).isEqualTo(reserva.getId());
        assertThat(response.estado()).isEqualTo("PENDIENTE");
        assertThat(response.costoTotal()).isEqualTo(300.0);
    }

    @Test
    void toResponseList_mapeaCadaElemento() {
        List<ReservaResponse> responses = mapper.toResponseList(List.of(nuevaReserva(), nuevaReserva()));

        assertThat(responses).hasSize(2)
                .allSatisfy(response -> assertThat(response.estado()).isEqualTo("PENDIENTE"));
    }

    @Test
    void toResponse_conNull_devuelveNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    private static Reserva nuevaReserva() {
        Cliente cliente = new Cliente("Ana Torres", "ana@hotel.com");
        return new Reserva(
                cliente,
                new HabitacionEstandar("101", 2, 150.0, 2),
                new RangoFechas(LocalDateTime.of(2026, 5, 1, 14, 0), LocalDateTime.of(2026, 5, 3, 14, 0)));
    }
}
