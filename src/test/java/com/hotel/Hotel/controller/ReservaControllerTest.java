package com.hotel.Hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hotel.Hotel.dto.request.CrearReservaRequest;
import com.hotel.Hotel.dto.response.ReservaResponse;
import com.hotel.Hotel.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReservaControllerTest {

    private ReservaService reservaService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reservaService = mock(ReservaService.class);
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(new ReservaController(reservaService))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void crearReserva_devuelve201ConUriYBody() throws Exception {
        UUID id = UUID.randomUUID();
        when(reservaService.crear(any(CrearReservaRequest.class))).thenReturn(new ReservaResponse(
                id, "Ana Torres", "101",
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), 300.0, "PENDIENTE"));

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":"%s","habitacionId":"%s","fechaInicio":"2026-05-01T14:00:00","fechaFin":"2026-05-03T14:00:00"}"""
                                .formatted(UUID.randomUUID(), UUID.randomUUID())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/reservas/" + id)))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.costoTotal").value(300.0));

        verify(reservaService).crear(any(CrearReservaRequest.class));
    }

    @Test
    void listarReservas_devuelve200ConLaLista() throws Exception {
        when(reservaService.listarTodas()).thenReturn(List.of(new ReservaResponse(
                UUID.randomUUID(), "Ana Torres", "101",
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), 300.0, "CONFIRMADA")));

        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("CONFIRMADA"));
    }

    @Test
    void buscarPorId_devuelve200ConLaReserva() throws Exception {
        UUID id = UUID.randomUUID();
        when(reservaService.obtenerPorId(id)).thenReturn(new ReservaResponse(
                id, "Ana Torres", "101",
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), 300.0, "PENDIENTE"));

        mockMvc.perform(get("/api/reservas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.habitacionNumero").value("101"));
    }
}
