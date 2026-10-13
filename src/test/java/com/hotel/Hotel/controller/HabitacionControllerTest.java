package com.hotel.Hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hotel.Hotel.domain.EstadoHabitacion;
import com.hotel.Hotel.domain.TipoHabitacion;
import com.hotel.Hotel.dto.request.CrearHabitacionEstandarRequest;
import com.hotel.Hotel.dto.request.CrearSuitePresidencialRequest;
import com.hotel.Hotel.dto.response.HabitacionEstandarResponse;
import com.hotel.Hotel.dto.response.HabitacionResponse;
import com.hotel.Hotel.dto.response.SuitePresidencialResponse;
import com.hotel.Hotel.service.HabitacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

class HabitacionControllerTest {

    private HabitacionService habitacionService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        habitacionService = mock(HabitacionService.class);
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(new HabitacionController(habitacionService))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void crearEstandar_devuelve201ConUriYBody() throws Exception {
        UUID id = UUID.randomUUID();
        when(habitacionService.crearEstandar(any(CrearHabitacionEstandarRequest.class)))
                .thenReturn(new HabitacionEstandarResponse(
                        id, "101", 2, 150.0, EstadoHabitacion.DISPONIBLE, TipoHabitacion.ESTANDAR, 2));

        mockMvc.perform(post("/api/habitaciones/estandar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numero":"101","capacidadMaxima":2,"precioPorNoche":150.0,"camasIndividuales":2}"""))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/habitaciones/" + id)))
                .andExpect(jsonPath("$.numero").value("101"))
                .andExpect(jsonPath("$.tipo").value("ESTANDAR"))
                .andExpect(jsonPath("$.camasIndividuales").value(2));

        verify(habitacionService).crearEstandar(any(CrearHabitacionEstandarRequest.class));
    }

    @Test
    void crearSuite_devuelve201ConUriYBody() throws Exception {
        UUID id = UUID.randomUUID();
        when(habitacionService.crearSuite(any(CrearSuitePresidencialRequest.class)))
                .thenReturn(new SuitePresidencialResponse(
                        id, "PH-1", 4, 900.0, EstadoHabitacion.DISPONIBLE, TipoHabitacion.SUITE, true, true));

        mockMvc.perform(post("/api/habitaciones/suites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numero":"PH-1","capacidadMaxima":4,"precioPorNoche":900.0,"incluyeMayordomo":true,"jacuzziPrivado":true}"""))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/habitaciones/" + id)))
                .andExpect(jsonPath("$.numero").value("PH-1"))
                .andExpect(jsonPath("$.tipo").value("SUITE"))
                .andExpect(jsonPath("$.incluyeMayordomo").value(true));

        verify(habitacionService).crearSuite(any(CrearSuitePresidencialRequest.class));
    }

    @Test
    void listarHabitaciones_devuelve200ConLaLista() throws Exception {
        when(habitacionService.listarTodas()).thenReturn(List.of(
                new HabitacionEstandarResponse(UUID.randomUUID(), "101", 2, 150.0,
                        EstadoHabitacion.DISPONIBLE, TipoHabitacion.ESTANDAR, 2),
                new SuitePresidencialResponse(UUID.randomUUID(), "PH-1", 4, 900.0,
                        EstadoHabitacion.OCUPADA, TipoHabitacion.SUITE, true, true)));

        mockMvc.perform(get("/api/habitaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tipo").value("ESTANDAR"))
                .andExpect(jsonPath("$[1].tipo").value("SUITE"));
    }

    @Test
    void buscarPorId_devuelve200ConLaHabitacion() throws Exception {
        UUID id = UUID.randomUUID();
        when(habitacionService.obtenerPorId(id)).thenReturn(new HabitacionEstandarResponse(
                id, "101", 2, 150.0, EstadoHabitacion.DISPONIBLE, TipoHabitacion.ESTANDAR, 2));

        mockMvc.perform(get("/api/habitaciones/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.numero").value("101"));
    }
}
