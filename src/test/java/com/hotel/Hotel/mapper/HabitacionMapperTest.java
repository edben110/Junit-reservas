package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.EstadoHabitacion;
import com.hotel.Hotel.domain.Habitacion;
import com.hotel.Hotel.domain.HabitacionEstandar;
import com.hotel.Hotel.domain.SuitePresidencial;
import com.hotel.Hotel.domain.TipoHabitacion;
import com.hotel.Hotel.dto.response.HabitacionEstandarResponse;
import com.hotel.Hotel.dto.response.HabitacionResponse;
import com.hotel.Hotel.dto.response.SuitePresidencialResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HabitacionMapperTest {

    private final HabitacionMapper mapper = new HabitacionMapperImpl();

    @Test
    void toResponse_mapeaUnaHabitacionEstandar() {
        HabitacionResponse response = mapper.toResponse(
                new HabitacionEstandar("101", 2, 150.0, 2));

        assertThat(response).isInstanceOf(HabitacionEstandarResponse.class);
        HabitacionEstandarResponse estandar = (HabitacionEstandarResponse) response;
        assertThat(estandar.numero()).isEqualTo("101");
        assertThat(estandar.capacidadMaxima()).isEqualTo(2);
        assertThat(estandar.precioPorNoche()).isEqualTo(150.0);
        assertThat(estandar.estado()).isEqualTo(EstadoHabitacion.DISPONIBLE);
        assertThat(estandar.tipo()).isEqualTo(TipoHabitacion.ESTANDAR);
        assertThat(estandar.camasIndividuales()).isEqualTo(2);
    }

    @Test
    void toResponse_mapeaUnaSuitePresidencial() {
        HabitacionResponse response = mapper.toResponse(
                new SuitePresidencial("PH-1", 4, 900.0, true, true));

        assertThat(response).isInstanceOf(SuitePresidencialResponse.class);
        SuitePresidencialResponse suite = (SuitePresidencialResponse) response;
        assertThat(suite.numero()).isEqualTo("PH-1");
        assertThat(suite.tipo()).isEqualTo(TipoHabitacion.SUITE);
        assertThat(suite.incluyeMayordomo()).isTrue();
        assertThat(suite.jacuzziPrivado()).isTrue();
    }

    @Test
    void toResponse_conTipoNoSoportado_lanzaExcepcion() {
        Habitacion desconocida = new Habitacion("999", 1, 10.0) {
        };

        assertThatThrownBy(() -> mapper.toResponse(desconocida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no soportado");
    }

    @Test
    void toResponseList_mapeaCadaSubtipo() {
        List<HabitacionResponse> responses = mapper.toResponseList(List.of(
                new HabitacionEstandar("101", 2, 150.0, 2),
                new SuitePresidencial("PH-1", 4, 900.0, true, true)));

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).tipo()).isEqualTo(TipoHabitacion.ESTANDAR);
        assertThat(responses.get(1).tipo()).isEqualTo(TipoHabitacion.SUITE);
    }
}
